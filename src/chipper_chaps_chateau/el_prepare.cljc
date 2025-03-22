(ns chipper-chaps-chateau.el-prepare
  (:require [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(defn prepare [db]
  (let [current-color (db/get-global db :current-color)
        chips (db/get-chips db)
        winner (victory/did-someone-win? chips)]
    {:current-color (or winner current-color)
     :game-won? winner
     :cells
     (->> chips
          (partition 3)
          (mapv
           #(mapv (fn [{:keys [chip/size chip/color chip/idx]}]
                    {:size size
                     :color color
                     :actions
                     (when (and (not winner)
                                (nil? color))
                       [[:action/transact [{:chip/idx idx
                                            :chip/color current-color}
                                           (db/->global-tx :current-color
                                                           (next-color current-color))]]])})
                  %)))}))
