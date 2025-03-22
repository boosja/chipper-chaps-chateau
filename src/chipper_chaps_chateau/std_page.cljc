(ns chipper-chaps-chateau.std-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(defn ->cells [chips get-actions]
  (->> chips
       (partition 3)
       (mapv
        #(mapv (fn [{:keys [chip/size chip/color] :as chip}]
                 {:size size
                  :color color
                  :actions (get-actions chip)})
               %))))

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
    (list (vis/player-box (or winner current-color)
                          winner)
          [:div.wrapper
           [::vis/board.board
            {::vis/data
             (->cells chips get-actions)}
            [::vis/cell.cell
             [::vis/chip.chip]]]])))
