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
     :tooltip "🙋 Human\n🤖 Bot"}))

(defn variant [db]
  (let [variant (:settings/variant (ds/entity db :settings))]
    {:sm true
     :actions [[::set :settings/variant (if (= :four-player variant)
                                          :two-player :four-player)]]
     :icon (if (= :four-player variant)
             "🧑‍🧑‍🧒‍🧒" "👥")
     :tooltip "👥 Two-player\n🧑‍🧑‍🧒‍🧒 Four-player"}))

(defn color-mode [db]
  (let [colorblind? (:settings/colorblind? (ds/entity db :settings))]
    {:sm true
     :actions [[::set :settings/colorblind? (not colorblind?)]]
     :icon (if colorblind? "🖌️" "🎨")
     :tooltip "🎨 Default\n🖌️ Colorblind"}))

(def difficulty {:pointless "😅"
                 :elementary "🤗"
                 :easy "😌"
                 :hard "😓"
                 :expert "😰"})

(def tooltip "😅 pointless\n🤗 elementary\n😌 easy\n😓 hard\n😰 expert")

(defn ->d1 []
  {:sm true
   :actions [[:action/navigate :route/d1]
             [:board/reset :dim/one]]
   :icon (:expert difficulty)
   :tooltip tooltip})

(defn ->d2 []
  {:sm true
   :actions [[:action/navigate :route/d2]
             [:board/reset :dim/two]]
   :icon (:pointless difficulty)
   :tooltip tooltip})

(defn ->d3 []
  {:sm true
   :actions [[:action/navigate :route/d3]
             [:board/reset :dim/three]]
   :icon (:elementary difficulty)
   :tooltip tooltip})

(defn ->d4 []
  {:sm true
   :actions [[:action/navigate :route/d4]
             [:board/reset :dim/four]]
   :icon (:easy difficulty)
   :tooltip tooltip})

(defn ->d5 []
  {:sm true
   :actions [[:action/navigate :route/d5]
             [:board/reset :dim/five]]
   :icon (:hard difficulty)
   :tooltip tooltip})
