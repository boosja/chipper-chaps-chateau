(ns chipper-chaps-chateau.bartial-scenes
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [portfolio.replicant :refer-macros [defscene]]))

(defscene bartial
  [:div
   [::vis/bartial.flex
    {::vis/data {:showcase {:class :blue
                            :text "Game"}
                 :right [{:sm true :icon "🙋‍♂️"}
                         {:sm true :icon "🧑‍🧑‍🧒‍🧒"}
                         {:icon "⚙️"}]}}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   [::vis/bartial.flex
    {::vis/data {:showcase {:class :red
                            :text "Winner"}
                 :left [{:icon "🎉"}]
                 :right [{:sm true :icon "🙋‍♂️"}
                         {:sm true :icon "🧑‍🧑‍🧒‍🧒"}
                         {:icon "🔄"}]}}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   [::vis/bartial.flex
    {::vis/data {:showcase {:class :tie
                            :text "Tie"}
                 :left [{:icon "💪"}]
                 :right [{:sm true :icon "🙋‍♂️"}
                         {:sm true :icon "🧑‍🧑‍🧒‍🧒"}
                         {:icon "🔄"}]}}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   [::vis/bartial.flex
    {::vis/data {:showcase {:class :green
                            :text "Rules"}
                 :right [{:icon "🙅‍♂️"}]}}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   [::vis/bartial.flex
    {::vis/data {:showcase {:class :yellow
                            :text "Bot"}
                 :right [{:sm true :icon "🤖"}
                         {:sm true :icon "🧑‍🧑‍🧒‍🧒"}
                         {:icon "⚙️"}]}}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]

   [::vis/bartial.flex
    {::vis/data {:showcase {:class :blue
                            :text "Two player"}
                 :right [{:sm true :icon "🙋‍♂️"}
                         {:sm true :icon "👥"}
                         {:icon "⚙️"}]}}
    [::vis/showcase]
    [::vis/icon]
    [::vis/space]
    [::vis/icon]]])
