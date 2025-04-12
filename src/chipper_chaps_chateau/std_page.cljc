(ns chipper-chaps-chateau.std-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.components.bar :refer [bar]]
            [clojure.string :as str]))

(def next-color
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})

(defn perform-action [db [action & args]]
  (when (= ::pick action)
    (let [current-color (db/get-global db :current-color)]
      [[:effect/transact [{:chip/idx (:chip/idx (first args))
                           :chip/color current-color}
                          (db/->global-tx :current-color
                                          (next-color current-color))]]])))

(defn prepare-bar [winner current-color]
  {:left {:icon "🤨"
          :actions [[:action/transact [(db/->global-tx :location :rules)]]]}
   :right {:icon "⚙️"
           :actions [[:action/transact [(db/->global-tx :location :settings)]]]}
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
  (let [current-color (db/get-global db :current-color)
        chips (db/get-chips db)
        winner (victory/did-someone-win? chips)]
    {:bar-props (prepare-bar winner current-color)
     :chips chips
     :get-actions (fn [chip]
                    (when (and (not winner)
                               (nil? (:chip/color chip)))
                      [[::pick chip]]))}))

(defn render [{:keys [bar-props chips get-actions]}]
  (list (bar bar-props)
        (vis/el-chateau get-actions chips)))
