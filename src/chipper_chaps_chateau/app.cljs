(ns chipper-chaps-chateau.app
  (:require [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.id :as id]
            [chipper-chaps-chateau.rules-page :as rules-page]
            [chipper-chaps-chateau.settings-page :as settings-page]
            [chipper-chaps-chateau.std-page :as std-page]
            [chipper-chaps-chateau.victory :as victory]
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

(def locations #{:std :rules :settings :4d})
(def variants #{:four-player :two-player})

(def txes (concat [{:db/ident :location :location :std}
                   {:db/ident :rules/show-all? :rules/show-all? false}
                   {:db/ident :settings
                    :settings/enable-bot false
                    :settings/variant :four-player}
                   {:db/id "game"
                    :game/id (id/gen!)
                    :game/current-color :blue
                    :game/chips (id/-ilize! :chip/id (chips/create-chips))}
                   {:db/ident :app/state
                    :app/current-game "game"}
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

(defn process-effect [conn [effect & args]]
  (apply prn 'Execute effect args)
  (case effect
    :effect/transact (apply ds/transact conn args)))

(defn perform-actions [db actions]
  (mapcat (fn [action]
            (or (std-page/perform-action db action)
                (case (first action)
                  :action/transact
                  [(into [:effect/transact] (rest action))]

                  (prn "⚠️ Unknown action"))))
          actions))

(comment
  (def db (ds/db conn))

  (def cs (db/get-chips db))
  cs

  (victory/did-someone-win? cs)
  )

(def pages {:std [std-page/el-prepzi std-page/render]
            :rules [rules-page/el-prepzi rules-page/render]
            :settings [settings-page/el-prepzi settings-page/render]
            :4d (fn [_] [:div "Coming soon..."])})

(defn app [db]
  (let [location (db/get-global db :location)
        [prep render] (get pages location)]
    [:main
     [:h1 "Chipper Chap's Chateau"]
     (render (prep db))]))

(defn ^:dev/after-load start []
  (js/console.log "[START]")
  (d/set-dispatch! (fn [_ actions]
                     (->> (perform-actions (ds/db conn) actions)
                          (run! #(process-effect conn %)))))
  (add-watch conn :app
             (fn [_ _ _ _]
               (d/render (js/document.getElementById "app")
                         (app (ds/db conn)))))
  (ds/transact! conn [{:init true}]))

(defn init []
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "[STOP]"))
