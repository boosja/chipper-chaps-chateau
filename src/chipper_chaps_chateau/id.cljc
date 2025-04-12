(ns chipper-chaps-chateau.id)

(comment
  (apply str (shuffle (set "chipper-chaps_chateau23456789")))
  )

;; Put in datascript!
(def id-alphabet "ausch5ir87e_362p9-t4")
(def base (count id-alphabet))
(def counter (atom (* base base)))

(defn gen! []
  (->> (swap! counter inc)
       (iterate #(/ % base))
       (map long)
       (take-while pos?)
       (map #(mod % base))
       (map #(nth id-alphabet %))
       (apply str)))

(comment
  (repeatedly 200 gen!)
  (gen!)
  )

(defn -ilize! [k coll]
  (map #(assoc % k (gen!)) coll))
