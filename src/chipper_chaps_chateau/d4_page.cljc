(ns chipper-chaps-chateau.d4-page
  (:require
   [chipper-chaps-chateau.la-visual :as vis]
   [clojure.string :as str]
   [chipper-chaps-chateau.settings :as settings]
   [chipper-chaps-chateau.db :as db]
   [chipper-chaps-chateau.d3-page :as d3-page]))

(defn get-color-name [color theme]
  (if theme
    (name (get {:blue :blue
                :red :orange
                :green :teal
                :yellow :purple}
               color))
    (name color)))

(defn prepare-showcase [winner current-color theme]
  {:class (if winner
            (name winner)
            (name current-color))
   :text (cond (= winner :tie)
               "Wow, you tied"

               winner
               (str (str/capitalize (get-color-name winner theme)) " is the winner")

               :else
               (str (get-color-name current-color theme) " player's turn"))})

(defn leftward-icons [winner]
  [{:icon (cond (= winner :tie) "💪"
                winner "🎉")}])

(defn rightward-icons [db winner]
  (into (settings/prepare db)
        [(if winner
           {:actions [[::reset-game]]
            :icon "🔄"
            :tooltip "Reset game"}
           {:actions [[:action/navigate :route.rules/summary]]
            :icon "📖"
            :tooltip "Rules"})]))


(defn prepare [db]
  (let [game (db/current-game db)
        settings (db/settings db)
        current-color (:game/current-color game)
        chips (partition-all 27 (sort-by (juxt :w :x :y :z) (:game/chips game)))
        #_#_winner (victory/did-someone-win? chips)
        theme (when (:settings/colorblind? settings)
                "colorblind")]
    {:bar-props {:showcase (prepare-showcase nil current-color theme)
                 :left (leftward-icons nil)
                 :right (rightward-icons db nil)}
     :theme theme
     :chips chips
     :get-actions (fn [chip] [[::d3-page/pick chip]])}))

(defn render [{:keys [bar-props theme chips get-actions]}]
  [:section {:class (cond-> ["grid"]
                      theme (conj theme))}
   [::vis/bartial.flex {::vis/data bar-props}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   (when (< (count chips) 3)
     [:div.red
      "Not enough chips"])

   [:div.flex
    (for [board chips]
      (vis/el-chateau get-actions board))]])
