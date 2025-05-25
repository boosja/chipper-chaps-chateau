(ns chipper-chaps-chateau.components.bar
  (:require [replicant.alias :refer [defalias]]
            [replicant.hiccup :as hiccup]
            [clojure.string :as str]))

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

(defn prepare-left-icons [winner]
  [{:icon (cond (= winner :tie) "💪"
                winner "🎉")}])

(defn prepare-right-icons []
  [{:actions [[:action/navigate :route.rules/summary]]
    :icon "📖"
    :tooltip "Rules"}])

(defalias space []
  [:span.expand])

(defalias showcase [attrs text]
  (let [{:keys [class actions]} (-> attrs ::data)]
   [:div.current {:class (if actions
                           (conj ["pointer" "lighten"] class)
                           class)
                  :on {:click actions}}
    text]))

(defalias icon [attrs content]
  (let [data (::data attrs)]
    [:span {:on {:click (:actions data)}
            :title (:tooltip data)
            :class (cond-> ["pointer"]
                     (:sm data) (conj "icon-sm")
                     (nil? (:sm data)) (conj "icon"))}
     content]))

(defalias bar [attrs children]
  (into
   [:div attrs]
   (map-indexed
    (fn [idx child]
      (case (first child)
        ::showcase (hiccup/update-attrs
                    [::showcase (-> attrs ::data :showcase :text)]
                    assoc ::data (-> attrs ::data :showcase))
        ::icon (map #(hiccup/update-attrs
                      [::icon (:icon %)]
                      assoc ::data %)
                    (if (< idx 2)
                      (-> attrs ::data :left)
                      (-> attrs ::data :right)))
        ::space child))
    children)))
