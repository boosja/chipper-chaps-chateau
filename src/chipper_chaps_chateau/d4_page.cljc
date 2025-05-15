(ns chipper-chaps-chateau.d4-page
  (:require
   [chipper-chaps-chateau.la-visual :as vis]
   [chipper-chaps-chateau.db :as db]
   [chipper-chaps-chateau.d3-page :as d3-page]
   [chipper-chaps-chateau.components.bar :as bar]))

(defn prepare [db]
  (let [game (db/current-game db)
        settings (db/settings db)
        current-color (:game/current-color game)
        chips (partition-all 27 (sort-by (juxt :w :x :y :z) (:game/chips game)))
        #_#_winner (victory/did-someone-win? chips)
        theme (when (:settings/colorblind? settings)
                "colorblind")]
    {:bar-props {:showcase (bar/prepare-showcase nil current-color theme)
                 :left (bar/prepare-left-icons nil)
                 :right (bar/prepare-right-icons db nil)}
     :theme theme
     :chips chips
     :get-actions (fn [chip] [[::d3-page/pick chip]])}))

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
