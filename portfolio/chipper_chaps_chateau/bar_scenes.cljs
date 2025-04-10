(ns chipper-chaps-chateau.bar-scenes
  (:require [chipper-chaps-chateau.components.bar :as bar]
            [portfolio.replicant :refer-macros [defscene]]))

(defscene bar
  (bar/bar {:left {:icon "🤨"}
            :right {:icon "⚙️"}
            :banner {:text "Bar text"
                     :class "blue"}}))

(defscene with-banner-action
  (bar/bar {:left {:icon "🤨"}
            :right {:icon "⚙️"}
            :banner {:text "You can click me"
                     :class "blue"
                     :actions [[:action/dummy :one]]}}))

(defscene with-revelry
  (bar/bar {:left {:icon "🤨"}
            :right {:icon "⚙️"}
            :revelry "🎉"
            :banner {:text "Winner"
                     :class "blue"}}))

(defscene different-color
  [:div
   (bar/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Blue"
                      :class "blue"}})
   (bar/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Red"
                      :class "red"}})
   (bar/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Green"
                      :class "green"}})
   (bar/bar {:left {:icon "🤨"}
             :right {:icon "⚙️"}
             :banner {:text "Yellow"
                      :class "yellow"}})])

(defscene close
  (bar/bar {:left {:icon "🙅‍♂️"}
            :right {:icon "🙅‍♂️"}
            :banner {:text "Close"
                     :class "blue"}}))
