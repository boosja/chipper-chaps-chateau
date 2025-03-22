(ns chipper-chaps-chateau.la-visual
  (:require [replicant.alias :refer [defalias]]
            [replicant.hiccup :as hiccup]))

(defn box [{:keys [color text actions]}]
  [:div.box.m-2.h-3 {:class (when actions ["pointer" "darken"])
                     :on {:click actions}}
   [:div.current {:class (name color)}
    text]])

(defn icon-box [{:keys [color text icon]}]
  [:div.box.m-2.h-3
   [:span.tada icon]
   [:div.current {:class (name color)}
    text]
   [:span.tada icon]])

(defalias board [{::keys [data] :as attrs} children]
  (into
   [:section attrs]
   (for [trio-data data]
     (hiccup/update-attrs
      (first children) assoc
      ::data trio-data))))

(defalias cell [{::keys [data] :as attrs} children]
  (into
   [:div attrs]
   (for [chip-data data]
     (hiccup/update-attrs
       (first children) assoc
       ::data chip-data))))

(def size->class {:lg "large"
                  :md "medium"
                  :sm "small"})

(defalias chip [{::keys [data] :as attrs}]
  (hiccup/update-attrs
   [:button attrs]
   (fn [attrs-map]
     (-> attrs-map
         (update :class conj
                 (size->class (:size data))
                 (name (or (:color data) "white")))
         (assoc-in [:on :click] (:actions data))))))
