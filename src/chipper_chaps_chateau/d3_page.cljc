(ns chipper-chaps-chateau.d3-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]
            [clojure.string :as str]
            [chipper-chaps-chateau.settings :as settings]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(defn pick-effects [db chip-id]
  (let [game (db/current-game db)]
   [[:effect/transact [{:chip/id (:chip/id chip-id)
                        :chip/color (:game/current-color game)}
                       {:game/id (:game/id game)
                        :game/current-color (next-color (:game/current-color game))}]]]))

(defn deferred-bot-move-effects [db ms]
  (let [settings (db/settings db)
        amount (cond-> 0
                 (:settings/enable-bot settings) inc
                 (and (:settings/enable-bot settings)
                      (= :four-player (:settings/variant settings))) (+ 2))]
    (remove nil? [(when (< 0 amount)
                    [:effect/defer ms (into [] (repeat amount [::bot-move]))])])))

(defn bot-move-effects [db]
  (let [game (db/current-game db)]
   (if (victory/did-someone-win? (:game/chips game))
     []
     (let [next-move (victory/pick-next-move victory/wins (:game/chips game))]
       [[:effect/transact [{:chip/id (:chip/id next-move)
                            :chip/color (:game/current-color game)}
                           {:game/id (:game/id game)
                            :game/current-color (next-color (:game/current-color game))}]]]))))

(defn reset-game-effects [_db]
  [[:effect/transact [{:db/id "new-game"
                       :game/id [:data-require :id/gen]
                       :game/current-color :blue
                       :game/chips [:data-require :id.gen/chips]}
                      {:db/ident :app/state
                       :app/current-game "new-game"}]]])

(defn perform-action [db [action & args]]
  (case action
    ::pick (pick-effects db (first args))
    ::deferred-bot-move (deferred-bot-move-effects db (first args))
    ::bot-move (bot-move-effects db)
    ::reset-game (reset-game-effects db)
    nil))

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
            :icon "🔄"}
           {:actions [[:action/navigate :route.rules/summary]]
            :icon "📖"})]))

(defn prepare [db]
  (let [game (db/current-game db)
        settings (db/settings db)
        current-color (:game/current-color game)
        chips (sort-by (juxt :x :y :z) (:game/chips game))
        winner (victory/did-someone-win? chips)
        theme (when (:settings/colorblind? settings)
                "colorblind")]
    {:bar-props {:showcase (prepare-showcase winner current-color theme)
                 :left (leftward-icons winner)
                 :right (rightward-icons db winner)}
     :theme theme
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[::pick chip]
                       [::deferred-bot-move 300]]))}))

(defn render [{:keys [bar-props theme chips get-actions]}]
  [:section {:class (cond-> ["grid"]
                      theme (conj theme))}
   [::vis/bartial.flex {::vis/data bar-props}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   (vis/el-chateau get-actions chips)])
