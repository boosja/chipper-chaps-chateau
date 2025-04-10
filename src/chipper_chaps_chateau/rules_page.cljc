(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.components.bar :refer [bar]]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.chips :as chips]))

(defn prepare-bar [show-all?]
  {:left {:icon "🙅‍♂️"
          :actions [[:action/transact [(db/->global-tx :location :std)]]]}
   :right {:icon "⚙️"
           :actions [[:action/transact [(db/->global-tx :location :settings)]]]}
   :banner {:text (if show-all? "Every possible win" "How to win")
            :class "green"
            :actions [[:action/transact
                       [(db/->global-tx :rules/show-all? (not show-all?))]]]}})

(defn el-prepzi [db]
  (let [show-all? (db/get-global db :rules/show-all?)
        chips (chips/create-chips)
        filtered [0 4 8 10 22 28 42 47 35]]
    {:bar-props (prepare-bar show-all?)
     :rule-boards (if show-all?
                    (map #(chips/replace-with chips %)
                         victory/wins)
                    (map #(chips/replace-with chips (nth victory/wins %))
                         filtered))
     }))

(defn render [{:keys [rule-boards bar-props]}]
  (list (bar bar-props)
        [:div.rules
         (for [chips rule-boards]
           (vis/el-chateau nil chips))]))
