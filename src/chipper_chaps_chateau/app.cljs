(ns chipper-chaps-chateau.app
  (:require [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.d3-page :as d3-page]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.id :as id]
            [chipper-chaps-chateau.rules-page :as rules-page]
            [chipper-chaps-chateau.settings :as settings]
            [chipper-chaps-chateau.victory :as victory]
            [clojure.walk :as walk]
            [datascript.core :as ds]
            [replicant.dom :as d]))

;; 4d version
;; undo
;; score moves (bot)
;; time barrier
;; know when game is won ✓
;; - also when tied ✓
;; show color on hover? no
;; clean up and alias ✓
;; play multiple games at the same time (stupid)
;; list all previous games under current
;; icon-bar that shows settings and better iconicity
;; remember settings in localstorage
;; better defer handling

(declare dispatch)
(declare perform-actions)
(declare process-effect)

(def txes (concat [{:db/ident :settings
                    :settings/enable-bot false
                    :settings/variant :four-player
                    :settings/colorblind? false}
                   {:db/id "game"
                    :game/id (id/gen!)
                    :game/current-color :blue
                    :game/chips (id/-ilize! :chip/id (chips/create-chips))}
                   {:db/ident :app/state
                    :app/current-game "game"
                    :app/location :route/d3}
                   {:db/id "player one"
                    :player/color :blue
                    :chip.lg/count 3
                    :chip.md/count 3
                    :chip.sm/count 3}]))

(def schema {:chip/id {:db/cardinality :db.cardinality/one
                        :db/unique :db.unique/identity}

             :app/current-game {:db/type :db.type/ref
                                  :db/cardinality :db.cardinality/one}

             :game/id {:db/cardinality :db.cardinality/one
                       :db/unique :db.unique/identity}
             :game/chips {:db/type :db.type/ref
                          :db/cardinality :db.cardinality/many}})

(defonce conn
  (let [conn (ds/create-conn schema)]
    (ds/transact! conn txes)
    conn))

(comment
  (def db (ds/db conn))

  (def cs (db/get-chips db))
  cs

  ;; Show custom chips
  (defn show-custom-chips [chips]
    (ds/transact conn [{:db/id "new-game"
                        :game/id (id/gen!)
                        :game/current-color :blue
                        :game/chips (id/-ilize! :chip/id chips)}
                       {:db/ident :app/state
                        :app/current-game "new-game"}]))

  (show-custom-chips (chips/colored-chateaus))
  (show-custom-chips (chips/colored-chateaus-switch :blue))
  (show-custom-chips (chips/colored-chateaus-switch :red))
  (show-custom-chips (chips/colored-chateaus-switch :green))
  (show-custom-chips (chips/colored-chateaus-switch :yellow))
  (show-custom-chips (chips/colored-chateaus-switch-cycle))
  (show-custom-chips (victory/heat-mapped-chips))

  ;; Reset game
  (show-custom-chips (chips/create-chips))

  (victory/did-someone-win? cs)
  )

(defn perform-actions [db actions]
  (mapcat (fn [action]
            (or (d3-page/perform-action db action)
                (settings/perform-action db action)
                (case (first action)
                  :action/navigate
                  [[:effect/transact [{:db/ident :app/state
                                       :app/location (second action)}]]]

                  (prn "⚠️ Unknown action:" (first action)))))
          actions))

(def refiners {:id/gen id/gen!
               :id.gen/chips #(id/-ilize! :chip/id (chips/create-chips))})

(defn refine [txes]
  (-> (fn [x]
        (cond
          (and (vector? x) (= :data-require (first x)))
          ((get refiners (second x)))

          :else x))
      (walk/postwalk txes)))

(defn defer-actions [ms actions]
  (letfn [(dispatch-next [remaining idx]
            (when-let [action (first remaining)]
              (js/setTimeout
               #(do
                  (dispatch nil [action])
                  (dispatch-next (rest remaining) (inc idx)))
               ms)))]
    (dispatch-next actions 0)))

(defn process-effect [conn [effect & args]]
  (apply prn 'Execute effect args)
  (case effect
    :effect/transact (apply ds/transact conn (refine args))
    :effect/defer (defer-actions (first args) (second args))))

(defn dispatch [_ actions]
  (->> (perform-actions (ds/db conn) actions)
       (run! #(process-effect conn %))))

(def routes {:route/d3 [d3-page/prepare d3-page/render]
             :route.rules/summary [rules-page/prepare rules-page/render]
             :route.rules/all [rules-page/prepare rules-page/render]

             :route/d4 (fn [_] [:div "Coming soon..."])})

(defn app [db]
  (let [location (db/location db)
        [prepare render] (get routes location)]
    [:main
     [:h1 "Chipper Chap's Chateau"]
     (render (prepare db))]))

(defn ^:dev/after-load start []
  (js/console.log "[START]")
  (d/set-dispatch! dispatch)
  (add-watch conn :app (fn [_ _ _ _]
                         (d/render (js/document.getElementById "app")
                                   (app (ds/db conn)))))
  (ds/transact! conn [{:init true}]))

(defn init []
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "[STOP]"))
