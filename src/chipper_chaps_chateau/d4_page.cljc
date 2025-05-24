(ns chipper-chaps-chateau.d4-page
  (:require
   [chipper-chaps-chateau.la-visual :as vis]
   [chipper-chaps-chateau.db :as db]
   [chipper-chaps-chateau.components.bar :as bar]
   [chipper-chaps-chateau.victory :as victory]))

(defn prepare [db]
  (let [game (db/current-game db)
        settings (db/settings db)
        current-color (:game/current-color game)
        chips (sort-by (comp vec reverse :point) (:game/chips game))
        parted-chips (partition-all 27 chips)
        winner (victory/did-someone-win?-2 chips)
        theme (when (:settings/colorblind? settings)
                "colorblind")]
    {:bar-props {:showcase (bar/prepare-showcase winner current-color theme)
                 :left (bar/prepare-left-icons winner)
                 :right (bar/prepare-right-icons db winner)}
     :theme theme
     :chips parted-chips
     :get-actions (fn [chip]
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[:board/select-chip chip]]))}))

(defn render [{:keys [bar-props theme chips get-actions]}]
  [:section {:class (cond-> ["grid"]
                      theme (conj theme))}
   [::bar/bar.flex {::bar/data bar-props}
    [::bar/showcase]
    [::bar/icon]
    [::bar/space]
    [::bar/icon]]

   (when (< (count chips) 3)
     [:div.red
      "Not enough chips"])

   [:div.flex
    (for [board chips]
      (vis/el-chateau get-actions board))]])
