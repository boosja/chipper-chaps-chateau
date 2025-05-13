(ns chipper-chaps-chateau.chips)

(defn find-circle-center [x y r pad]
  (let [d (* 2 r)]
    [(- (* (+ pad d) x) r)
     (- (* (+ pad d) y) r)]))

(def svg-circles
  (let [size 1800
        lg-radius (/ (* size 0.30) 2)
        md-radius (* lg-radius 0.65)
        sm-radius (* lg-radius 0.30)
        pad (/ (- size (* lg-radius 2 3)) 4)]
    (apply merge (for [y (range 1 4)
                       x (range 1 4)]
                   (let [[cx cy] (find-circle-center x y lg-radius pad)
                         shadow (* size 0.0067)]
                     {(str y x 1) [[:circle {:cx (- cx shadow)
                                             :cy (+ cy shadow)
                                             :r lg-radius}]
                                   [:circle {:id (str x y 1)
                                             :cx cx :cy cy :r lg-radius}]]

                      (str y x 2) [[:circle {:cx (- cx (- pad) shadow)
                                             :cy (+ cy (- pad) shadow)
                                             :r md-radius}]
                                   [:circle {:id (str x y 2)
                                             :cx (+ cx pad)
                                             :cy (- cy pad)
                                             :r md-radius}]]

                      (str y x 3) [[:circle {:cx (- cx (- (* 2 pad)) shadow)
                                             :cy (+ cy (- (* 2 pad)) shadow)
                                             :r sm-radius}]
                                   [:circle {:id (str x y 3)
                                             :cx (+ cx (* 2 pad))
                                             :cy (- cy (* 2 pad))
                                             :r sm-radius}]]})))))

(defn create-chips []
  (for [y (range 1 4)
        x (range 1 4)
        z (range 1 4)]
    {:x x
     :y y
     :z z
     :chip/size (get {1 :lg 2 :md 3 :sm} z)
     :svg/circle (get svg-circles (str y x z))}))

(defn ->xyz [chip]
  (select-keys chip [:x :y :z]))

(defn is-in? [winning-line chip]
  (contains? winning-line (->xyz chip)))

(defn replace-with [chips winning-line]
  (mapv (fn [c]
          (if (is-in? winning-line c)
            (assoc c :chip/color :blue)
            c))
        chips))

;; ################################################################
;; ## CUSTOM COLORED BOARDS

(def clridx {0 :blue
             1 :red
             2 :green
             3 :yellow})

(defn colored-chateaus
  "Each chateau has unique color"
  []
  (->> (create-chips)
       (partition-all 3)
       (map-indexed (fn [i chateau]
                      (map #(assoc % :chip/color (get clridx (mod i 4)))
                           chateau)))
       flatten))

(defn take-mod [n coll start]
  (let [len (count coll)]
    (mapv #(nth coll (mod (+ start %) len))
          (range n))))

(defn ->idx-mapper [v]
  [(map-indexed #(vector %2 %1) v)
   (map-indexed #(vector %1 %2) v)])

(->> [:red :green :yellow]
     ->idx-mapper
     (apply concat)
     (into {}))

(defn colored-chateaus-switch
  "Each chateau with switching colors (using only 3)"
  [starting-color]
  (let [all-colors [:blue :red :green :yellow]
        colors (->> (take-mod 3 all-colors (.indexOf all-colors starting-color))
                    ->idx-mapper
                    (apply concat)
                    (into {}))
        starting-color-idx (get colors starting-color)]
    (->> (create-chips)
         (sort-by (juxt :y :z :x))
         (partition-all 3)
         (map-indexed (fn [i chateau]
                        (map #(assoc % :chip/color
                                     (get colors (mod (+ i starting-color-idx)
                                                      3)))
                             chateau)))
         flatten)))

(defn colored-chateaus-switch-cycle
  "Each chateau with switcing colors cycling through starting colors"
  []
  (let [all-colors [:blue :red :green :yellow]
        colors (map (fn [starting-color]
                      (->> (take-mod 3 all-colors (.indexOf all-colors starting-color))
                           ->idx-mapper
                           (apply concat)
                           (into {})))
                    all-colors)]
    (->> (create-chips)
         (sort-by (juxt :y :x :z))
         (partition-all 3)
         (map-indexed (fn [i chateau]
                        (map #(assoc % :chip/color
                                     (get (nth colors (mod (+ (dec (:x %))
                                                              (* 3 (dec (:y %))))
                                                           4))
                                          (mod (dec (:z %)) 3)))
                             chateau)))
         flatten)))
