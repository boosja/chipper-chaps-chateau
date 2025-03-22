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
  (let [current-color (db/get-global db :current-color)
        chips (db/get-chips db)
        winner (victory/did-someone-win? chips)
        get-actions (fn [chip]
                     (when (and (not winner)
                                (nil? (:chip/color chip)))
                       [[:action/transact
                         [{:chip/idx (:chip/idx chip)
                           :chip/color current-color}
                          (db/->global-tx :current-color
                                          (next-color current-color))]]]))]
    (list (if winner
            (vis/icon-box {:color winner
                           :icon "🎉"
                           :text (str (str/capitalize (name winner)) " is the winner")})
            (vis/box {:color current-color
                      :text "Current player"}))
          [:div.wrapper
           [::vis/board.board
            {::vis/data
             (db/->cells chips get-actions)}
            [::vis/cell.cell
             [::vis/chip.chip]]]])))
