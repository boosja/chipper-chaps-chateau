(ns chipper-chaps-chateau.la-visual
  (:require [clojure.string :as str]
            [replicant.alias :refer [defalias]]
            [replicant.hiccup :as hiccup]))

(defn player-box [color game-won?]
  (let [clr (name color)]
    (if game-won?
      [:div.box
       [:span.tada "🎉"]
       [:div.current {:class clr}
        (str (str/capitalize clr) " is the winner")]
       [:span.tada "🎉"]]

      [:div.box
       [:div.current {:class clr}
        "Current player"]])))

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
   [:div attrs]
   (fn [attrs-map]
     (-> attrs-map
         (update :class conj
                 (size->class (:size data))
                 (name (or (:color data) "white")))
         (assoc-in [:on :click] (:actions data))))))
