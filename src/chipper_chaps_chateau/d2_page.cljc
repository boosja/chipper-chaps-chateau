(ns chipper-chaps-chateau.d2-page
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.components.bar :as bar]
            [chipper-chaps-chateau.settings :as settings]
            [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.wins :as wins]))

(defn prepare [db]
  (let [game (db/current-game db)
        current-color (:game/current-color game)
        chips (sort-by :point (:game/chips game))
        winner (victory/has-winner? chips wins/d2)]
    {:bar-props {:showcase (bar/prepare-showcase winner current-color nil)
                 :left (bar/prepare-left-icons winner)
                 :right (-> [(settings/->d3)
                             {:sm true
                              :actions [[:board.d2/reset]]
                              :icon "🔄"
                              :tooltip "Reset game"}]
                            (into (bar/prepare-right-icons)))}
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[:board/select-chip chip (if (= :blue current-color)
                                                  :red :blue)]]))}))

(defn render [{:keys [bar-props chips get-actions]}]
  [:main
   [:h1 "Chipper Chap's Chateau"]
   [:section.grid
    [::bar/bar.flex {::bar/data bar-props}
     [::bar/showcase]
     [::bar/icon]
     [::bar/space]
     [::bar/icon]]

    (vis/el-chateau get-actions chips)]])
