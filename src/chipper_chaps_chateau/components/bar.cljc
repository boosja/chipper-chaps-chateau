(ns chipper-chaps-chateau.components.bar)

(defn bar [{:keys [left right revelry banner]}]
  [:div.flex
   [:span.icon.pointer {:on {:click (:actions left)}}
    (:icon left)]
   [:span.expand]
   (when revelry
     [:span.icon.revelry revelry])
   [:div.current {:class (:class banner)}
    (:text banner)]
   (when revelry
     [:span.icon.revelry revelry])
   [:span.expand]
   [:span.icon.pointer {:on {:click (:actions right)}}
    (:icon right)]])
