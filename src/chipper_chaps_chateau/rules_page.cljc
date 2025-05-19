(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.components.bar :as bar]
            [chipper-chaps-chateau.wins :as wins]))

(defn prepare-bar [show-all?]
  {:showcase {:text (if show-all? "Every possible win" "How to win")
              :class "green"
              :actions [[:action/navigate (if show-all?
                                            :route.rules/summary
                                            :route.rules/all)]]}
   :right [{:icon "🙅‍♂️"
            :actions [[:action/navigate :route/d3]]}]})

(defn prepare [db]
  (let [show-all? (= :route.rules/all (db/location db))
        chips-d3 (chips/create-chips)
        chips-d4 (chips/create-chips-4d)
        filtered [0 4 8 12 22 40 36 47 29]]
    {:bar-props (prepare-bar show-all?)
     :rule-boards-d3 (if show-all?
                    (map #(chips/add-winning-line chips-d3 % :blue)
                         wins/d3)
                    (map #(chips/add-winning-line chips-d3 (nth wins/d3 %) :blue)
                         filtered))
     :rule-boards-d4 (if show-all?
                       (map #(partition-all 27 %)
                            (map #(chips/add-winning-line chips-d4 % :blue)
                                 wins/d4))
                       (map #(partition-all 27 %)
                            (map #(chips/add-winning-line chips-d4 % :blue)
                                 (filter (fn [win]
                                           (apply not= (map :w win))) wins/d4))))}))

(defn render [{:keys [bar-props rule-boards-d3 rule-boards-d4]}]
  (list [::bar/bar.flex {::bar/data bar-props}
         [::bar/showcase]
         [::bar/icon]
         [::bar/space]
         [::bar/icon]]

        [:div.rules
         (for [chips rule-boards-d3]
           [:div.container
            (vis/el-chateau nil chips)])]

        [:h2 "4D-unique rules:"]
        [:div.rules-d4
         (for [[w1 w2 w3] rule-boards-d4]
           [:div.container.rules
            (vis/el-chateau nil w1)
            (vis/el-chateau nil w2)
            (vis/el-chateau nil w3)])]))
