(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.chips :as chips]))

(defn el-prepzi [db]
  (let [location (db/get-global db :location)
        show-all? (db/get-global db :rules/show-all?)
        chips (chips/create-chips)
        filtered [0 4 8 10 22 28 42 47 35]]
    {:rule-boards (if show-all?
                    (map #(chips/replace-with chips %)
                         victory/wins)
                    (map #(chips/replace-with chips (nth victory/wins %))
                         filtered))
     :close [[:action/transact [(db/->global-tx :location (if (= location :rules)
                                                            :std
                                                            :rules))]]]
     :bar {:toggle [[:action/transact [(db/->global-tx :rules/show-all?
                                                       (not show-all?))]]]
           :text (if show-all? "Every possible win" "How to win")}}))

(defn render [{:keys [rule-boards close bar]}]
  (list [:div.flex.m-1
         [:span.icon.pointer
          {:on {:click close}}
          "🙅‍♂️"]
         [:span.expand]
         [:div.current.green.pointer.lighten
          {:on {:click (:toggle bar)}}
          (:text bar)]
         [:span.expand]
         [:span.icon "⚙️"]]

        [:div.rules
         (for [chips rule-boards]
           (vis/el-chateau nil chips))]))
