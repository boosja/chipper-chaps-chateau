(ns chipper-chaps-chateau.d3-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.components.bar :as bar]
            [chipper-chaps-chateau.wins :as wins]
            [chipper-chaps-chateau.bot :as bot]
            [chipper-chaps-chateau.settings :as settings]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

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
     (let [next-move (bot/pick-next-move wins/d3 (:game/chips game))]
       [[:effect/transact [{:chip/id (:chip/id next-move)
                            :chip/color (:game/current-color game)}
                           {:game/id (:game/id game)
                            :game/current-color (next-color (:game/current-color game))}]]]))))

(defn perform-action [db [action & args]]
  (case action
    ::deferred-bot-move (deferred-bot-move-effects db (first args))
    ::bot-move (bot-move-effects db)
    nil))

(defn prepare [db]
  (let [game (db/current-game db)
        settings (db/settings db)
        current-color (:game/current-color game)
        chips (sort-by :point (:game/chips game))
        winner (victory/did-someone-win? chips)
        theme (when (:settings/colorblind? settings)
                "colorblind")]
    {:bar-props {:showcase (bar/prepare-showcase winner current-color theme)
                 :left (bar/prepare-left-icons winner)
                 :right (-> [(settings/bot db)
                             (settings/variant db)
                             (settings/color-mode db)
                             (settings/->d4)
                             {:sm true
                              :actions [[:board.d3/reset]]
                              :icon "🔄"
                              :tooltip "Reset game"}]
                            (into (bar/prepare-right-icons)))}
     :theme theme
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner) (nil? (:chip/color chip)))
                      [[:board/select-chip chip]
                       [::deferred-bot-move 300]]))}))

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
