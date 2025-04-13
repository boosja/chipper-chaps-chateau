(ns chipper-chaps-chateau.bar-scenes
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [portfolio.replicant :refer-macros [defscene]]))

(defscene bar
  (vis/bar {:left {:icon "🤨"}
            :right {:icon "⚙️"}
            :banner {:text "Bar text"
                     :class "blue"}}))

(defscene with-banner-action
  (vis/bar {:left {:icon "🤨"}
            :right {:icon "⚙️"}
            :banner {:text "You can click me"
                     :class "blue"
                     :actions [[:action/dummy :one]]}}))

(defscene with-revelry
  (vis/bar {:left {:icon "🤨"}
            :right {:icon "⚙️"}
            :revelry "🎉"
            :banner {:text "Winner"
                     :class "blue"}}))

(defscene different-color
  [:div
   (vis/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Blue"
                      :class "blue"}})
   (vis/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Red"
                      :class "red"}})
   (vis/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Green"
                      :class "green"}})
   (vis/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Yellow"
                      :class "yellow"}})])

(defscene close
  (vis/bar {:left {:icon "🙅‍♂️"}
            :right {:icon "🙅‍♂️"}
            :banner {:text "Close"
                     :class "blue"}}))
