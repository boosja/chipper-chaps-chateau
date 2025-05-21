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
                 (merge {:horizontal (conj (:horizontal wins) [c b a])
                         :vertical (conj (:vertical wins) [a c b])
                         :depth (conj (:depth wins) [b a c])})

                 (= c 1)
                 (merge {:diagonal-horizontal (conj (:diagonal-horizontal wins) [b a b])
                         :diagonal-horizontal-rev (conj (:diagonal-horizontal-rev wins) [(- 4 b) a b])
                         :diagonal-vertical (conj (:diagonal-vertical wins) [a b b])
                         :diagonal-vertical-rev (conj (:diagonal-vertical-rev wins) [a (- 4 b) b])
                         :diagonal-desc (conj (:diagonal-desc wins) [b b a])
                         :diagonal-asc (conj (:diagonal-asc wins) [(- 4 b) b a])})

                 (and (= c 1) (= b 1))
                 (merge {:diagonal-depth-desc (conj (:diagonal-depth-desc wins) [a a a])
                         :diagonal-depth-desc-rev (conj (:diagonal-depth-desc-rev wins) [a a (- 4 a)])
                         :diagonal-depth-asc (conj (:diagonal-depth-asc wins) [a (- 4 a) a])
                         :diagonal-depth-asc-rev (conj (:diagonal-depth-asc-rev wins) [(- 4 a) a a])}))]
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

(defn collinear? [p1 p2 p3]
  (let [v1 (mapv - p2 p1)
        v2 (mapv - p3 p1)]
    (or
     (every? zero? v1)
     (every? zero? v2)
     (let [ratios (keep (fn [[a b]]
                          (when (not (and (zero? b) (zero? a)))
                            (if (not (zero? a))
                              (/ b a)
                              0)))
                        (map vector v1 v2))]
       (and (not-every? zero? ratios)
            (apply = ratios))))))

(defn ->coord-vec [p-map]
  (vec (keep #(get p-map %) [:x :y :z :w :v :u :t])))

(defn find-collinear-triplets-maps [points]
  (for [i (range (count points))
        j (range (inc i) (count points))
        k (range (inc j) (count points))
        :let [p1 (nth points i)
              p2 (nth points j)
              p3 (nth points k)]
        :when (collinear? (->coord-vec p1) (->coord-vec p2) (->coord-vec p3))]
    #{p1 p2 p3}))

(defn find-collinear-triplets [points]
  (for [i (range (count points))
        j (range (inc i) (count points))
        k (range (inc j) (count points))
        :let [p1 (nth points i)
              p2 (nth points j)
              p3 (nth points k)]
        :when (collinear? p1 p2 p3)]
    #{p1 p2 p3}))

(def d4 (find-collinear-triplets (for [w (range 1 4)
                                       y (range 1 4)
                                       x (range 1 4)
                                       z (range 1 4)]
                                   [x y z w])))
