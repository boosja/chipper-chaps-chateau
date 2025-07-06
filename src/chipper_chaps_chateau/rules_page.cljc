(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.components.bar :as bar]
            [chipper-chaps-chateau.wins :as wins]))

(defn prepare [db]
  (let [show-all? (= :route.rules/all (db/location db))
        back-to-4d? (< 27 (-> (db/current-game db) :game/chips count))
        back-to-5d? (< 81 (-> (db/current-game db) :game/chips count))
        chips-d3 (chips/create-chips :dim/three)
        filtered [0 4 8 12 22 40 36 47 29]
        chips-d4 (chips/create-chips :dim/four)
        filtered-d4 [7 8 9 10 11 12]
        chips-d5 (chips/create-chips :dim/five)
        filtered-d5 [25 87 133 30]]
    {:bar-props {:showcase {:text (if show-all? "Every possible win" "How to win")
                            :class "green"
                            :actions [[:action/navigate (if show-all?
                                                          :route.rules/summary
                                                          :route.rules/all)]]}
                 :right [{:icon "🙅‍♂️"
                          :actions (cond
                                     back-to-5d? [[:action/navigate :route/d5]]
                                     back-to-4d? [[:action/navigate :route/d4]]
                                     :else [[:action/navigate :route/d3]])}]}

     :rule-boards-d3 (if show-all?
                       (map #(chips/add-winning-line chips-d3 % :blue)
                            wins/d3)
                       (map #(chips/add-winning-line chips-d3 (nth wins/d3 %) :blue)
                            filtered))

     :rule-boards-d4 (when-not show-all?
                       (->> (map #(chips/add-winning-line chips-d4 (nth wins/d4 %) :blue)
                                 filtered-d4)
                            (map #(partition-all 27 %))))

     :rule-boards-d5 (when-not show-all?
                       (->> (map #(chips/add-winning-line chips-d5 (nth wins/d5 %) :blue)
                                 filtered-d5)
                            (map #(partition-all 27 %))))}))

(defn render-dimensional-presupposition []
  [:div.dimensional-presupposition
   (sequence (map (comp #(vector :div.header (str % "D"))
                        inc))
             (range 5))

   [:div.container
    [::vis/board.board
     {::vis/chips (chips/create-chips :dim/one)}
     [:circle.shadow]
     [:circle]]]

   [:div.container
    [::vis/board.board
     {::vis/chips (chips/create-chips :dim/two)}
     [:circle.shadow]
     [:circle]]]

   [:div.container
    [::vis/board.board
     {::vis/chips (chips/create-chips :dim/three)}
     [:circle.shadow]
     [:circle]]]

   [:div.container.rules
    (repeat 3 [::vis/board.board])
    (repeat 3 [::vis/board.board
               {::vis/chips (chips/create-chips :dim/three)}
               [:circle.shadow]
               [:circle]])
    (repeat 3 [::vis/board.board])]

   [:div.container.rules
    (repeat 9 [::vis/board.board
               {::vis/chips (chips/create-chips :dim/three)}
               [:circle.shadow]
               [:circle]])]])

(defn render [{:keys [bar-props rule-boards-d3 rule-boards-d4 rule-boards-d5]}]
  [:main
   [:h1 "Chipper Chap's Chateau"]
   [::bar/bar.flex {::bar/data bar-props}
    [::bar/showcase]
    [::bar/icon]
    [::bar/space]
    [::bar/icon]]

   (render-dimensional-presupposition)

   [:h2 "3D rules:"]
   [:div.rules
    (for [chips rule-boards-d3]
      [:div.container
       [::vis/board.board
        {::vis/chips chips}
        [:circle.shadow]
        [:circle]]])]

   (when rule-boards-d4
     (list
      [:h2 "4D rules:"]
      [:div.rules-d4
       (for [boards rule-boards-d4]
         [:div.container.rules
          (for [board boards]
            [::vis/board.board
             {::vis/chips board}
             [:circle.shadow]
             [:circle]])])]))

   (when rule-boards-d5
     (list
      [:h2 "5D rules:"]
      [:div.rules-d4
       (for [boards rule-boards-d5]
         [:div.container.rules
          (for [board boards]
            [::vis/board.board
             {::vis/chips board}
             [:circle.shadow]
             [:circle]])])]))])

(comment

  ;; Find index of winning line
  (.indexOf wins/d5 #{[2 2 2 1 1] [2 2 2 2 2] [2 2 2 3 3]})
  (.indexOf wins/d5 #{[3 1 1 1 1] [2 1 1 2 2] [1 1 1 3 3]})
  (.indexOf wins/d5 #{[2 1 1 1 1] [2 1 1 2 2] [2 1 1 3 3]})
  (.indexOf wins/d5 #{[1 1 1 1 1] [2 2 2 2 2] [3 3 3 3 3]})

  ;; WIP sorting
  (->> (filter #(apply not= (map last %)) wins/d4)
       (sort wins/compare-wins)
       (map #(vector % (chips/add-winning-line (chips/create-chips :dim/four) % :blue)))
       (map #(update % 1 (fn [b] (partition-all 27 b)))))

  ;; all 4D wins with ids
  (->> (map-indexed #(vector %1 (chips/add-winning-line (chips/create-chips :dim/four) %2 :blue))
                    wins/d4)
       (map #(update % 1 (fn [b] (partition-all 27 b)))))

  #_
  [:div.rules-d4
   (for [[id [w1 w2 w3]] rule-boards-d4]
     [:div.container.rules {:id id}
      (vis/el-chateau nil w1)
      (vis/el-chateau nil w2)
      (vis/el-chateau nil w3)])]

  ;; all 5D wins with ids
  (->> (map-indexed #(vector %1 (chips/add-winning-line (chips/create-chips :dim/five) %2 :blue))
                    (take 200 wins/d5))
       (map #(update % 1 (fn [b] (partition-all 27 b)))))

  #_
  [:div.rules-d4
   (for [[id boards] rule-boards-d5]
     [:div.container.rules {:id id}
      (for [board boards]
        (vis/el-chateau nil board))])]

  )
