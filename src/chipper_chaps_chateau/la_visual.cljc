(ns chipper-chaps-chateau.la-visual
  (:require [replicant.alias :refer [defalias]]
            [replicant.hiccup :as hiccup]
            [chipper-chaps-chateau.chips :as chips]))

(defalias board [{::keys [chips get-actions] :as attrs} children]
  (into
   (-> [:svg {:viewBox "0 0 1800 1800" :xmlns "http://www.w3.org/2000/svg"
              :stroke-width "4"
              :stroke "grey"
              :fill "white"}]
       (hiccup/update-attrs merge attrs))
   (map (fn [chip]
          (let [cs (->> (:point chip) chips/get-circle-key chips/circle-placements)]
            (list (hiccup/update-attrs (first children) conj (first cs))
                  (hiccup/update-attrs (second children) conj
                                       (assoc (second cs)
                                              :class (name (or (:chip/color chip) "white"))
                                              :on {:click (when get-actions
                                                            (get-actions chip))})))))
        chips)))

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
