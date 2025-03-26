(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.chips :as chips]))

(defn render [db]
  (let [location (db/get-global db :location)
        show-all? (db/get-global db :rules/show-all?)
        chips (chips/create-chips)
        filtered [0 4 8 10 22 28 42 47 35]]
    (list [:div.flex.m-1
           [:span.icon.pointer
            {:on {:click [[:action/transact [(db/->global-tx :location
                                                             (if (= location :rules)
                                                               :std
                                                               :rules))]]]}}
            "🙅‍♂️"]
           [:span.expand]
           [:div.current.green.pointer.lighten
            {:on {:click [[:action/transact [(db/->global-tx :rules/show-all?
                                                             (not show-all?))]]]}}
            (if show-all? "Every possible win" "How to win")]
           [:span.expand]
           [:span.icon "⚙️"]]

          [:div.rules
           (if show-all?
             (for [winning-line victory/wins]
               (vis/el-chateau (chips/replace-with chips winning-line)
                               (fn [_])))
             (for [w-idx filtered]
               (vis/el-chateau (chips/replace-with chips (nth victory/wins w-idx))
                               (fn [_]))))])))
