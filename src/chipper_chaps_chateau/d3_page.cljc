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
  (let [game (db/current-game db)]
    (case action
      ::pick
      [[:effect/transact [{:chip/id (:chip/id (first args))
                           :chip/color (:game/current-color game)}
                          {:game/id (:game/id game)
                           :game/current-color (next-color (:game/current-color game))}]]]

      ::deferred-bot-move
      (let [settings (db/settings db)
            amount (cond-> 0
                       (:settings/enable-bot settings) inc
                       (and (:settings/enable-bot settings)
                            (= :four-player (:settings/variant settings))) (+ 2))]
        (remove nil? [(when (< 0 amount)
                        [:effect/defer (into [] (repeat amount [::bot-move]))])]))

      ::bot-move
      (let [next-move (victory/pick-next-move victory/wins
                                              (:game/chips game)
                                              (:game/current-color game))]
        [[:effect/transact [{:chip/id (:chip/id next-move)
                             :chip/color (:game/current-color game)}
                            {:game/id (:game/id game)
                             :game/current-color (next-color (:game/current-color game))}]]])

      ::reset-game
      [[:effect/transact [{:db/id "new-game"
                           :game/id [:data-require :id/gen]
                           :game/current-color :blue
                           :game/chips [:data-require :id.gen/chips]}
                          {:db/ident :app/state
                           :app/current-game "new-game"}]]]
      nil)))

(defn prepare-bar [winner current-color]
  {:left (if winner
           {:icon "🔄"
            :actions [[::reset-game]]}
           {:icon "🤨"
            :actions [[:action/navigate :route.rules/summary]]})
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
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[::pick chip]
                       [::deferred-bot-move]]))}))

(defn render [{:keys [bar-props chips get-actions]}]
  (list (vis/bar bar-props)
        (vis/el-chateau get-actions chips)))
