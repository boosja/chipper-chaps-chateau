(ns chipper-chaps-chateau.components.bar
  (:require [replicant.alias :refer [defalias]]
            [replicant.hiccup :as hiccup]))

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
