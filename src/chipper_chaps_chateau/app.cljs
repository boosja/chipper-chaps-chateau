(ns chipper-chaps-chateau.app
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.el-prepare :as el-prepare]
            [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [datascript.core :as ds]
            [replicant.dom :as d]))

;; 4d version
;; undo
;; score moves (bot)
;; time barrier
;; know when game is won ✓
;; - also when tied
;; show color on hover?
;; clean up and alias ✓

(def txes (concat [{:db/ident :current-color
                    :current-color :blue}
                   {:db/id "player one"
                    :player/color :blue
                    :chip.lg/count 3
                    :chip.md/count 3
                    :chip.sm/count 3}]
                  (db/chips-txes)))

(def schema {:chip/idx {:db/cardinality :db.cardinality/one
                        :db/unique :db.unique/identity}})

(defonce conn
  (let [conn (ds/create-conn schema)]
    (ds/transact! conn txes)
    conn))

(defn actions-handler [metadata actions]
  (let [_ (:replicant/js-event metadata)]
    (doseq [[action & args] actions]
      (apply prn 'Execute action args)
      (case action
        :action/transact (apply ds/transact conn args)))))

(comment
  (def db (ds/db conn))

  (def cs (db/get-chips db))
  cs

  (->> cs (partition 3) (mapv (fn [[l m s]] [{:size :lg :color (:chip/color l :white)}
                                             {:size :md :color (:chip/color m :white)}
                                             {:size :sm :color (:chip/color s :white)}])))

  (victory/did-someone-win? cs)

  )

(defn app [db]
  (let [state (el-prepare/prepare db)]
    [:main
     [:h1 "Chipper Chap's Chateau"]
     (vis/player-box (:current-color state)
                     (:game-won? state))
     [:div.wrapper
      [::vis/board.board
       {::vis/data (:cells state)}
       [::vis/cell.cell
        [::vis/chip.chip]]]]]))

(defn ^:dev/after-load start []
  (js/console.log "[START]")
  (d/set-dispatch! actions-handler)
  (add-watch conn :app
             (fn [_ _ _ _]
               (d/render (js/document.getElementById "app")
                         (app (ds/db conn)))))
  (ds/transact! conn [{:init true}]))

(defn init []
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "[STOP]"))
