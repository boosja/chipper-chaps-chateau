(ns chipper-chaps-chateau.std-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]
            [clojure.string :as str]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(defn render [db]
  (let [location (db/get-global db :location)
        current-color (db/get-global db :current-color)
        chips (db/get-chips db)
        winner (victory/did-someone-win? chips)
        navigation [[:action/transact [(db/->global-tx :location
                                                        (if (= location :rules)
                                                          :std
                                                          :rules))]]]
        get-actions (fn [chip]
                      (when (and (not winner)
                                 (nil? (:chip/color chip)))
                        [[:action/transact
                          [{:chip/idx (:chip/idx chip)
                            :chip/color current-color}
                           (db/->global-tx :current-color
                                           (next-color current-color))]]]))]
    (list (cond
            (= winner :tie)
            [:div.flex
             [:span.icon.pointer {:on {:click navigation}} "🤨"]
             [:span.expand]
             [:span.icon.revelry "💪"]
             [:div.current {:class (name winner)}
              "Wow, you tied"]
             [:span.icon.revelry "💪"]
             [:span.expand]
             [:span.icon "⚙️"]]

            winner
            [:div.flex
             [:span.icon.pointer {:on {:click navigation}} "🤨"]
             [:span.expand]
             [:span.icon.revelry "🎉"]
             [:div.current {:class (name winner)}
              (str (str/capitalize (name winner)) " is the winner")]
             [:span.icon.revelry "🎉"]
             [:span.expand]
             [:span.icon "⚙️"]]

            :else
            [:div.flex
             [:span.icon.pointer {:on {:click navigation}} "🤨"]
             [:span.expand]
             [:div.current {:class (name current-color)}
              (str (name current-color) " player's turn")]
             [:span.expand]
             [:span.icon "⚙️"]])

          (vis/el-chateau get-actions chips))))
