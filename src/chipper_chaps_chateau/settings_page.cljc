(ns chipper-chaps-chateau.settings-page
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.components.bar :refer [bar]]
            [datascript.core :as ds]))

(defn prepare-bar []
  {:left {:icon "🙅‍♂️"
          :actions [[:action/transact [(db/->global-tx :location :std)]]]}
   :right {:icon "🙅‍♂️"
           :actions [[:action/transact [(db/->global-tx :location :std)]]]}
   :banner {:text "Settings"
            :class "yellow"}})

(defn el-prepzi [db]
  (let [settings (ds/entity db :settings)
        enable-bot (:settings/enable-bot settings)
        variant (:settings/variant settings)]
    {:bar-props (prepare-bar)

     :bot-enabled enable-bot
     :enable-bot [[:action/transact [{:db/ident :settings
                                      :settings/enable-bot (not enable-bot)}]]]
     :variant variant
     :set-variant [[:action/transact [{:db/ident :settings
                                       :settings/variant (if (= :four-player variant)
                                                           :two-player
                                                           :four-player)}]]]

     :settings settings}))

(defn render [{:keys [bar-props bot-enabled enable-bot variant set-variant]}]
  [:div
   (bar bar-props)

   [:table.settings
    [:tr
     [:td
      [:label.flex.item-border-b
       [:span.icon "🤖"] "Enable bot"]]
     [:td
      [:btn.icon.pointer {:on {:click enable-bot}}
       (if bot-enabled "✅" "❌")]]]

    [:tr
     [:td
      [:label.flex
       [:span.icon "👥"] "Players"]]
     [:td
      [:btn.icon.pointer {:on {:click set-variant}}
       (if (= :four-player variant)
         "✌️✌️" "☝️☝️")]]]

    [:tr
     [:td
      [:label.flex
       [:span.icon "🎲"] "Game"]]
     [:td
      [:btn
       "4D coming..."]]]]])
