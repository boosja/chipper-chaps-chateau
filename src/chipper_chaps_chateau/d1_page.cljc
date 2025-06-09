(ns chipper-chaps-chateau.d1-page
  (:require [chipper-chaps-chateau.components.bar :as bar]
            [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.settings :as settings]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.wins :as wins]))

(defn prepare [db]
  (let [game (db/current-game db)
        chips (sort-by :point (:game/chips game))
        winner (victory/has-winner? chips wins/d1)]
    {:bar-props {:showcase (bar/prepare-showcase winner :blue nil)
                 :left (bar/prepare-left-icons winner)
                 :right (-> [(settings/->d2)
                             {:sm true
                              :actions [[:board/reset :dim/one]]
                              :icon "🔄"
                              :tooltip "Reset game"}]
                            (into (bar/prepare-right-icons)))}
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[:board/select-chip chip :blue]]))}))

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
