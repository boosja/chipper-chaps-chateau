(ns chipper-chaps-chateau.app
  (:require [chipper-chaps-chateau.victory :as victory]
            [clojure.string :as str]
            [datascript.core :as ds]
            [replicant.dom :as d]))

;; 4d version
;; undo
;; score moves (bot)
;; time barrier
;; know when game is won ✓
;; - also when tied
;; show color on hover

(defn chips []
  (for [i (range 3)
        j (range 3)
        k (range 3)]
    (let [x (inc j)
          y (inc i)
          z (inc k)]
      {:x x :y y :z z :pawn/idx (+ (* 100 y) (* 10 x) (* 1 z))})))

(def txes (concat [{:db/id "player one"
                    :player/color :blue
                    :chip.lg/count 3
                    :chip.md/count 3
                    :chip.sm/count 3}
                   {:db/ident :current-color
                    :current-color :blue}]
                  (chips)))

(def schema {:player/color {:db/cardinality :db.cardinality/one
                            :db/unique :db.unique/identity}

             :player/pawns {:db/valueType :db.type/ref
                            :db/cardinality :db.cardinality/many}

             :pawn/idx {:db/cardinality :db.cardinality/one
                        :db/unique :db.unique/identity}})

(defonce conn
  (let [conn (ds/create-conn schema)]
    (ds/transact! conn txes)
    conn))

(comment

  (def db (ds/db conn))

  (->> (ds/q '[:find [?e ...]
               :where [?e :player/pawns]]
             db)
       (mapv #(ds/entity db %))
       (mapv #(into {} %))
       (mapv (fn [pl] (update pl :player/pawns (fn [ps] (mapv #(into {} %) ps))))))

  (->> (ds/entity db [:pawn/idx 3])
      (into {}))

  (->> (ds/q '[:find [?e ...]
               :where
               [?e :x ?x]]
             db)
       (map #(ds/entity db %))
       (sort-by :idx)
       (map #(into {} %)))

  (into {} (ds/entity db :current-color))
  (:current-color (ds/entity db :current-color))

  )

(defn get-current-color [db]
  (:current-color (ds/entity db :current-color)))

(defn actions-handler [metadata actions]
  (let [_ (:replicant/js-event metadata)]
    (doseq [[action & args] actions]
      (apply prn 'Execute action args)
      (case action
        :action/transact (ds/transact conn args)))))

(defn get-chips [db]
  (->> (ds/q '[:find [?e ...]
               :where
               [?e :x ?x]]
             db)
       (map #(->> % (ds/entity db) (into {})))
       (sort-by :pawn/idx)))

(defn ->cells [chips]
  (->> chips
       (partition 3)
       (mapv (comp vec flatten))))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(comment
  (def db (ds/db conn))

  (def cs (get-chips db))
  cs

  (->> cs
       (filter :colored)
       (group-by :colored)
       (victory/vals->sets)
       (some victory/has-three-in-a-row?))

  (victory/did-someone-win? cs)

  )

(defn click-action [chip current-color]
  [[:action/transact {:pawn/idx (:pawn/idx chip)
                      :colored current-color}]
   [:action/transact {:db/ident :current-color
                      :current-color (next-color current-color)}]])

(defn winner-section [winner]
  [:div.box.winner
   [:span.tada "🎉"]
   [:div.current {:class (and winner (name winner))}
    (str (str/capitalize (name winner)) " is the winner")]
   [:span.tada "🎉"]])

(defn app [db]
  (let [current-color (get-current-color db)
        chips (get-chips db)
        cells (->cells chips)
        winner (victory/did-someone-win? chips)]
    [:main
     [:h1 "Chipper Chap's Chateau"]
     (if winner
       (winner-section winner)
       [:div.box
        [:div.current {:class (name current-color)}
         "Current player"]])
     [:section.wrapper
      [:div.board
       (for [[large medium small] cells]
         [:div.cell
          [:div.chip.large
           {:class (:colored large :white)
            :on {:click (when (and (not winner) (nil? (:colored large)))
                          (click-action large current-color))}}]

          [:div.chip.medium
           {:class (:colored medium :white)
            :on {:click (when (and (not winner) (nil? (:colored medium)))
                          (click-action medium current-color))}}]

          [:div.chip.small
           {:class (:colored small :white)
            :on {:click (when (and (not winner) (nil? (:colored small)))
                          (click-action small current-color))}}]])]]]))

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
