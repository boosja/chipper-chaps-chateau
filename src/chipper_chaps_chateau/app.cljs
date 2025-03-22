(ns chipper-chaps-chateau.app
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.rules-page :as rules-page]
            [chipper-chaps-chateau.std-page :as std-page]
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

(def locations #{:std :rules :4d})

(def txes (concat [{:db/ident :current-color :current-color :blue}
                   {:db/ident :location :location :std}
                   {:db/ident :rules/show-all? :rules/show-all? false}
                   {:db/id "player one"
                    :player/color :blue
                    :chip.lg/count 3
                    :chip.md/count 3
                    :chip.sm/count 3}]
                  (db/create-chips)))

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

(def pages {:std #'std-page/render
            :rules #'rules-page/render
            :4d (fn [_] [:div "Coming soon..."])})

(defn app [db]
  (let [location (db/get-global db :location)
        render (get pages location)]
    [:main
     [:h1 "Chipper Chap's Chateau"]
     [:div.box.m-1
      [:button.nav-btn
       {:on {:click [[:action/transact [(db/->global-tx :location
                                                        (if (= location :rules)
                                                          :std
                                                          :rules))]]]}}
       (if (= location :rules)
         "< Back"
         "Rules")]]
     (render db)]))

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
