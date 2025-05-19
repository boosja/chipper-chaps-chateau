(ns chipper-chaps-chateau.wins)

(defn create-d3-wins []
  (loop [a 1
         b 1
         c 1
         wins {:horizontal []
               :vertical []
               :depth []
               :diagonal-horizontal []
               :diagonal-horizontal-rev []
               :diagonal-vertical []
               :diagonal-vertical-rev []
               :diagonal-desc []
               :diagonal-asc []
               :diagonal-depth-desc []
               :diagonal-depth-desc-rev []
               :diagonal-depth-asc []
               :diagonal-depth-asc-rev []}]
    (let [wins (cond-> wins
                 :yes-please
                 (merge {:horizontal (conj (:horizontal wins) {:x c :y b :z a})
                         :vertical (conj (:vertical wins) {:x a :y c :z b})
                         :depth (conj (:depth wins) {:x b :y a :z c})})

                 (= c 1)
                 (merge {:diagonal-horizontal (conj (:diagonal-horizontal wins) {:x b :y a :z b})
                         :diagonal-horizontal-rev (conj (:diagonal-horizontal-rev wins) {:x (- 4 b) :y a :z b})
                         :diagonal-vertical (conj (:diagonal-vertical wins) {:x a :y b :z b})
                         :diagonal-vertical-rev (conj (:diagonal-vertical-rev wins) {:x a :y (- 4 b) :z b})
                         :diagonal-desc (conj (:diagonal-desc wins) {:x b :y b :z a})
                         :diagonal-asc (conj (:diagonal-asc wins) {:x (- 4 b) :y b :z a})})

                 (and (= c 1) (= b 1))
                 (merge {:diagonal-depth-desc (conj (:diagonal-depth-desc wins) {:x a :y a :z a})
                         :diagonal-depth-desc-rev (conj (:diagonal-depth-desc-rev wins) {:x a :y a :z (- 4 a)})
                         :diagonal-depth-asc (conj (:diagonal-depth-asc wins) {:x a :y (- 4 a) :z a})
                         :diagonal-depth-asc-rev (conj (:diagonal-depth-asc-rev wins) {:x (- 4 a) :y a :z a})}))]
      (if (and (= a 3) (= b 3) (= c 3))
        (->> (reduce into [] [(:horizontal wins) (:vertical wins) (:depth wins)
                              (:diagonal-horizontal wins) (:diagonal-horizontal-rev wins)
                              (:diagonal-vertical wins) (:diagonal-vertical-rev wins)
                              (:diagonal-desc wins) (:diagonal-asc wins)
                              (:diagonal-depth-desc wins) (:diagonal-depth-desc-rev wins)
                              (:diagonal-depth-asc wins) (:diagonal-depth-asc-rev wins)])
             (partition-all 3)
             (mapv set))
        (recur (if (and (= c 3) (= b 3)) (inc a) a)
               (if (= c 3) (inc (mod b 3)) b)
               (inc (mod c 3))
               wins)))))

(def d3 (create-d3-wins))
(defn d4-unique-wins []
  (loop [a 1
         b 1
         c 1
         d 1
         wins {:straight []}]
    (let [wins (cond-> wins
                 :yes-please
                 (merge {:straight (conj (:straight wins) {:x c :y b :z a :w d})}))]
      (if (and (= a 3) (= b 3) (= c 3) (= d 3))
        (->> (:straight wins)
             (partition-all 3)
             (mapv set))
        (recur (if (and (= d 3) (= c 3) (= b 3)) (inc a) a)
               (if (and (= d 3) (= c 3)) (inc (mod b 3)) b)
               (if (= d 3) (inc (mod c 3)) c)
               (inc (mod d 3))
               wins)))))

(defn create-d4-wins []
  (into (->> (range 1 4)
             (map #(mapv (fn [winning-line]
                           (into #{} (map (fn [p] (assoc p :w %)) winning-line)))
                         d3))
             flatten
             vec)
        (d4-unique-wins)))

(comment

  (d4-unique-wins)
  (create-d4-wins)

  )
