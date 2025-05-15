(ns chipper-chaps-chateau.la-visual
  (:require [replicant.hiccup :as hiccup]))

(defn el-chateau [get-actions chips]
  [:svg.board {:viewBox "0 0 1800 1800" :xmlns "http://www.w3.org/2000/svg"
         :stroke-width "4"
         :stroke "grey"
         :fill "white"}
   (->> chips
        (map (fn [chip]
               (let [[shadow circle] (:svg/circle chip)]
                 [:g
                  (hiccup/update-attrs shadow assoc :class "shadow")
                  (hiccup/update-attrs
                   circle assoc
                   :class [(name (or (:chip/color chip) "white"))
                           "pointer" "darken"]
                   :on {:click (when get-actions (get-actions chip))})])))
        )])

(defn bar
  "Deprecated - Use bartial instead"
  [{:keys [left right revelry banner]}]
  [:div.flex
   [:span.icon.pointer {:on {:click (:actions left)}}
    (:icon left)]
   [:span.expand]
   (when revelry
     [:span.icon.revelry revelry])
   (if (:actions banner)
     [:button.current.pointer.lighten {:class (:class banner)
                                       :on {:click (:actions banner)}}
      (:text banner)]
     [:div.current {:class (:class banner)}
      (:text banner)])
   (when revelry
     [:span.icon.revelry revelry])
   [:span.expand]
   [:span.icon.pointer {:on {:click (:actions right)}}
    (:icon right)]])

(comment

  (set! *print-namespace-maps* false)

  )
