(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.components.bar :as bar]))

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
        chips (chips/create-chips)
        filtered [0 4 8 10 22 28 42 47 35]]
    {:bar-props (prepare-bar show-all?)
     :rule-boards (if show-all?
                    (map #(chips/replace-with chips %)
                         victory/wins)
                    (map #(chips/replace-with chips (nth victory/wins %))
                         filtered))}))

(defn render [{:keys [rule-boards bar-props]}]
  (list [::bar/bar.flex {::bar/data bar-props}
         [::bar/showcase]
         [::bar/icon]
         [::bar/space]
         [::bar/icon]]

        [:div.rules
         (for [chips rule-boards]
           (vis/el-chateau nil chips))]))
