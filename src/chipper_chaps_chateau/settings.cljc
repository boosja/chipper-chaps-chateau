(ns chipper-chaps-chateau.settings
  (:require [datascript.core :as ds]))

(defn perform-action [_db [action & args]]
  (when (= ::set action)
    [[:effect/transact [(into [:db/add [:db/ident :settings]] args)]]]))

(defn prepare [db]
  (let [settings (ds/entity db :settings)
        enable-bot (:settings/enable-bot settings)
        variant (:settings/variant settings)
        colorblind? (:settings/colorblind? settings)]
    [{:sm true
      :actions [[::set :settings/enable-bot (not enable-bot)]]
      :icon (if enable-bot "🤖" "🙋‍♂️")}
     {:sm true
      :actions [[::set :settings/variant (if (= :four-player variant)
                                           :two-player :four-player)]]
      :icon (if (= :four-player variant)
              "🧑‍🧑‍🧒‍🧒" "👥")}
     {:sm true
      :actions [[::set :settings/colorblind? (not colorblind?)]]
      :icon (if colorblind?
              "🖌️" "🎨")}]))
