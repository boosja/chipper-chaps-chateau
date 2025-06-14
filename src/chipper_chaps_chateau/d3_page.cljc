(ns chipper-chaps-chateau.d3-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.components.bar :as bar]
            [chipper-chaps-chateau.wins :as wins]
            [chipper-chaps-chateau.settings :as settings]))

(defn prepare [db]
  (let [game (db/current-game db)
        settings (db/settings db)
        current-color (:game/current-color game)
        chips (sort-by :point (:game/chips game))
        winner (victory/has-winner? chips wins/d3)
        theme (when (:settings/colorblind? settings)
                "colorblind")]
    {:bar-props {:showcase (bar/prepare-showcase winner current-color theme)
                 :left (bar/prepare-left-icons winner)
                 :right (-> [(settings/bot db)
                             (settings/variant db)
                             (settings/color-mode db)
                             (settings/->d4)
                             {:sm true
                              :actions [[:board/reset :dim/three]]
                              :icon "🔄"
                              :tooltip "Reset game"}]
                            (into (bar/prepare-right-icons)))}
     :theme theme
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[:board/select-chip chip]
                       [:bot/deferred-move 300]]))}))

(defn render [{:keys [bar-props theme chips get-actions]}]
  [:main
   [:h1 "Chipper Chap's Chateau"]
   [:section {:class (cond-> ["grid"]
                       theme (conj theme))}
    [::bar/bar.flex {::bar/data bar-props}
     [::bar/showcase]
     [::bar/icon]
     [::bar/space]
     [::bar/icon]]

    (vis/el-chateau get-actions chips)]])
