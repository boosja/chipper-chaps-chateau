(ns chipper-chaps-chateau.d3-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]
            [clojure.string :as str]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(defn perform-action [db [action & args]]
  (when (= ::pick action)
    (let [game (db/current-game db)]
      [[:effect/transact [{:chip/id (:chip/id (first args))
                           :chip/color (:game/current-color game)}
                          {:game/id (:game/id game)
                           :game/current-color (next-color (:game/current-color game))}]]])))

(defn prepare-bar [winner current-color]
  {:left {:icon "🤨"
          :actions [[:action/navigate :route.rules/summary]]}
   :right {:icon "⚙️"
           :actions [[:action/navigate :route/settings]]}
   :revelry (cond (= winner :tie) "💪"
                  winner "🎉")
   :banner {:text (cond (= winner :tie)
                        "Wow, you tied"

                        winner
                        (str (str/capitalize (name winner)) " is the winner")

                        :else
                        (str (name current-color) " player's turn"))
            :class (if winner
                     (name winner)
                     (name current-color))}})

(defn el-prepzi [db]
  (let [game (db/current-game db)
        current-color (:game/current-color game)
        chips (sort-by (juxt :x :y :z) (:game/chips game))
        winner (victory/did-someone-win? chips)]
    {:bar-props (prepare-bar winner current-color)
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner)
                               (nil? (:chip/color chip)))
                      [[::pick chip]]))}))

(defn render [{:keys [bar-props chips get-actions]}]
  (list (vis/bar bar-props)
        (vis/el-chateau get-actions chips)))
