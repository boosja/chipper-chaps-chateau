(ns chipper-chaps-chateau.id)

(defn begin-at [n base]
  (->> (repeat (dec n) base) ;; pow
       (apply *)))

(defn create-generator [n alphabet]
  (let [base (count alphabet)
        counter (atom (begin-at n base))]
    (fn []
      (->> (swap! counter inc)
           (iterate #(/ % base))
           (map long)
           (take-while pos?)
           (map #(mod % base))
           (map #(nth alphabet %))
           reverse
           (apply str)))))

(def gen! (create-generator 3 "ausch5ir87e_362p9-t4"))

(comment
  (apply str (shuffle (set "chipper-chaps_chateau23456789")))

  (repeatedly 200 gen!)
  (gen!)

  ;; Test out bases
  (->> #(let [alpha (apply str (shuffle (set "chipperchapschateau")))]
          [alpha (repeatedly 5 (create-generator 3 alpha))])
       (repeatedly 100)
       set
       (sort-by first))
  )

(defn -ilize! [k coll]
  (map #(assoc % k (gen!)) coll))
