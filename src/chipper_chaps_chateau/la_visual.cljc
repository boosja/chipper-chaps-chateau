(ns chipper-chaps-chateau.la-visual
  (:require [replicant.hiccup :as hiccup]))

(defn el-chateau [get-actions chips]
  [:svg {:viewBox "0 0 1800 1800" :xmlns "http://www.w3.org/2000/svg"
         :stroke-width "4"
         :stroke "grey"
         :fill "white"}
   (->> chips
        (map (fn [chip]
               (let [[shadow circle] (:svg/circle chip)]
                 [:g
                  (hiccup/update-attrs shadow assoc
                                       :class "shadow")
                  (hiccup/update-attrs circle assoc
                                       :class [(name (or (:chip/color chip) "white"))
                                               "pointer" "darken"]
                                       :on {:click (when get-actions
                                                     (get-actions chip))})])))
        )])
