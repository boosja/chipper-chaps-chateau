(ns chipper-chaps-chateau.settings
  (:require [datascript.core :as ds]))

(defn perform-action [_db [action & args]]
  (when (= ::set action)
    [[:effect/transact [(into [:db/add [:db/ident :settings]] args)]]]))

(defn bot [db]
  (let [enable-bot (:settings/enable-bot (ds/entity db :settings))]
    {:sm true
     :actions [[::set :settings/enable-bot (not enable-bot)]]
     :icon (if enable-bot "🤖" "🙋‍♂️")
     :tooltip "🙋 Human / 🤖 Bot"}))

(defn variant [db]
  (let [variant (:settings/variant (ds/entity db :settings))]
    {:sm true
     :actions [[::set :settings/variant]]
     :icon (if (= :four-player variant)
             "🧑‍🧑‍🧒‍🧒" "👥")
     :tooltip "🧑‍🧑‍🧒‍🧒 Four-player / 👥 Two-player"}))

(defn color-mode [db]
  (let [colorblind? (:settings/colorblind? (ds/entity db :settings))]
    {:sm true
     :actions [[::set :settings/colorblind? (not colorblind?)]]
     :icon (if colorblind? "🖌️" "🎨")
     :tooltip "🎨 Default / 🖌️ Colorblind"}))

(defn ->d3 []
  {:sm true
   :actions [[:action/navigate :route/d3]
             [:board.d3/reset]]
   :icon "😰"
   :tooltip "😌 easy / 😓 hard / 😰 expert"})

(defn ->d4 []
  {:sm true
   :actions [[:action/navigate :route/d4]
             [:board.d4/reset]]
   :icon "😌"
   :tooltip "😌 easy / 😓 hard / 😰 expert"})

(defn ->d5 []
  {:sm true
   :actions [[:action/navigate :route/d5]
             [:board.d5/reset]]
   :icon "😓"
   :tooltip "😌 easy / 😓 hard / 😰 expert"})
