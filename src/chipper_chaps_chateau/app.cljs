(ns chipper-chaps-chateau.app
  (:require [chipper-chaps-chateau.board :as board]
            [chipper-chaps-chateau.bot :as bot]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.d1-page :as d1-page]
            [chipper-chaps-chateau.d2-page :as d2-page]
            [chipper-chaps-chateau.d3-page :as d3-page]
            [chipper-chaps-chateau.d4-page :as d4-page]
            [chipper-chaps-chateau.d5-page :as d5-page]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.id :as id]
            [chipper-chaps-chateau.rules-page :as rules-page]
            [chipper-chaps-chateau.settings :as settings]
            [chipper-chaps-chateau.victory :as victory]
            [clojure.walk :as walk]
            [datascript.core :as ds]
            [replicant.dom :as d]))

;; undo
;; time barrier
;; play multiple games at the same time (stupid)
;; list all previous games under current
;; remember settings in localstorage
;; hashing board/state to url - see any board with url
;; aliasify vis/el-chateau
;; ✓ see through bot logic
;; add bot for the other dimensions too
;; add colors in the fourth and fifth dimensions
;; use colors in code, not css
;; migrate to tailwindcss
;; use phosphor icons?

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
                    :game/chips (id/-ilize! :chip/id (chips/create-chips :dim/three))}
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

(defn override-board! [chips]
    (ds/transact conn [{:db/id "new-game"
                        :game/id (id/gen!)
                        :game/current-color :blue
                        :game/chips (id/-ilize! :chip/id chips)}
                       {:db/ident :app/state
                        :app/current-game "new-game"}]))

(comment
  (def db (ds/db conn))

  (def current-game (db/current-game db))
  (->> (:game/chips current-game)
       (map #(into {} %))
       (sort-by (comp vec reverse :point))
       (partition-all 27))

  (def cs (db/get-chips db))
  cs

  (override-board! (chips/colored-chateaus))
  (override-board! (chips/colored-chateaus-switch :blue))
  (override-board! (chips/colored-chateaus-switch :red))
  (override-board! (chips/colored-chateaus-switch :green))
  (override-board! (chips/colored-chateaus-switch :yellow))
  (override-board! (chips/colored-chateaus-switch-cycle))
  (override-board! (victory/heat-mapped-chips))
  (override-board! (victory/heat-mapped-chips-4d))
  (override-board! (victory/heat-mapped-chips-5d))

  ;; Reset game
  (override-board! (chips/create-chips :dim/three))

  ;; Navigate
  (do (dispatch nil [[:action/navigate :route/d1]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/one))))
  (do (dispatch nil [[:action/navigate :route/d2]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/two))))
  (do (dispatch nil [[:action/navigate :route/d3]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/three))))
  (do (dispatch nil [[:action/navigate :route/d4]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/four))))
  (do (dispatch nil [[:action/navigate :route/d5]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/five))))

  )

(defn perform-actions [db actions]
  (mapcat (fn [action]
            (or (bot/perform-action db action)
                (board/perform-action db action)
                (settings/perform-action db action)
                (case (first action)
                  :action/navigate
                  [[:effect/transact [{:db/ident :app/state
                                       :app/location (second action)}]]]

                  (prn "⚠️ Unknown action:" (first action)))))
          actions))

(def refiners {:id/gen id/gen!
               :chips/gen #(id/-ilize! :chip/id (chips/create-chips %))})

(defn refine [txes]
  (-> (fn [x]
        (cond
          (and (vector? x) (= :data-require (first x)))
          (let [[_ refiner & args] x]
            ((get refiners refiner) (first args)))

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

(def routes {:route/d1 [d1-page/prepare d1-page/render]
             :route/d2 [d2-page/prepare d2-page/render]
             :route/d3 [d3-page/prepare d3-page/render]
             :route/d4 [d4-page/prepare d4-page/render]
             :route/d5 [d5-page/prepare d5-page/render]
             :route.rules/summary [rules-page/prepare rules-page/render]
             :route.rules/all [rules-page/prepare rules-page/render]})

(defn app [db]
  (let [location (db/location db)
        [prepare render] (get routes location)]
    (render (prepare db))))

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
