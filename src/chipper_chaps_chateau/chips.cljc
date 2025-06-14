(ns chipper-chaps-chateau.chips)

(defn comp-yzx []
  (fn [{[x y z] :point}]
    [y z x]))

(defn comp-yxz []
  (fn [{[x y z] :point}]
    [y x z]))

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
                     {(str x y 1) [[:circle {:cx (- cx shadow)
                                             :cy (+ cy shadow)
                                             :r lg-radius}]
                                   [:circle {:id (str x y 1)
                                             :cx cx :cy cy :r lg-radius}]]

                      (str x y 2) [[:circle {:cx (- cx (- pad) shadow)
                                             :cy (+ cy (- pad) shadow)
                                             :r md-radius}]
                                   [:circle {:id (str x y 2)
                                             :cx (+ cx pad)
                                             :cy (- cy pad)
                                             :r md-radius}]]

                      (str x y 3) [[:circle {:cx (- cx (- (* 2 pad)) shadow)
                                             :cy (+ cy (- (* 2 pad)) shadow)
                                             :r sm-radius}]
                                   [:circle {:id (str x y 3)
                                             :cx (+ cx (* 2 pad))
                                             :cy (- cy (* 2 pad))
                                             :r sm-radius}]]})))))

(defn cartesian-product [& colls]
  (if (empty? colls)
    [[]]
    (vec (for [x (first colls)
               xs (apply cartesian-product (rest colls))]
           (vec (conj xs x))))))

(defn vec-merge [v1 v2]
  (reduce-kv assoc v1 v2))

(defn vec-take [n v]
  (into [] (take n) v))

(defn create-chips*
  "Create chips for a board with n dimensions"
  [n]
  (->> (apply cartesian-product (repeat n (range 1 4)))
       (map (fn [p]
              {:point p
               :svg/circle
               (get svg-circles
                    (apply str (if (< (count p) 3)
                                 (vec-merge [nil 2 1] (vec-take 3 p))
                                 (vec-take 3 p))))}))))

(def ->n {:dim/one 1
          :dim/two 2
          :dim/three 3
          :dim/four 4
          :dim/five 5})

(defn create-chips [dim]
  (create-chips* (get ->n dim)))

(defn add-winning-line
  "Colors the winning line in the list of chips"
  [chips winning-line & [color]]
  (mapv (fn [chip]
          (if (some #{(:point chip)} winning-line)
            (assoc chip :chip/color (or color :blue))
            chip))
        chips))

;; ################################################################
;; ## Example boards
(def example-board [{:point [1 1 1], :chip/color :red}
                    {:point [1 1 2]}
                    {:point [1 1 3]}
                    {:point [1 2 1], :chip/color :red}
                    {:point [1 2 2]}
                    {:point [1 2 3]}
                    {:point [1 3 1]}
                    {:point [1 3 2]}
                    {:point [1 3 3], :chip/color :blue}
                    {:point [2 1 1]}
                    {:point [2 1 2]}
                    {:point [2 1 3]}
                    {:point [2 2 1]}
                    {:point [2 2 2], :chip/color :blue}
                    {:point [2 2 3]}
                    {:point [2 3 1]}
                    {:point [2 3 2]}
                    {:point [2 3 3]}
                    {:point [3 1 1], :chip/color :green}
                    {:point [3 1 2]}
                    {:point [3 1 3], :chip/color :green}
                    {:point [3 2 1]}
                    {:point [3 2 2]}
                    {:point [3 2 3]}
                    {:point [3 3 1], :chip/color :yellow}
                    {:point [3 3 2], :chip/color :yellow}
                    {:point [3 3 3]}])

;; ################################################################
;; ## CUSTOM COLORED BOARDS

(def clridx {0 :blue
             1 :red
             2 :green
             3 :yellow})

(defn colored-chateaus
  "Each chateau has unique color"
  []
  (->> (create-chips :dim/three)
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

(comment

  (->> [:red :green :yellow]
       ->idx-mapper
       (apply concat)
       (into {}))
  )

(defn colored-chateaus-switch
  "Each chateau with switching colors (using only 3)"
  [starting-color]
  (let [all-colors [:blue :red :green :yellow]
        colors (->> (take-mod 3 all-colors (.indexOf all-colors starting-color))
                    ->idx-mapper
                    (apply concat)
                    (into {}))
        starting-color-idx (get colors starting-color)]
    (->> (create-chips :dim/three)
         (sort-by (comp-yzx))
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
    (->> (create-chips :dim/three)
         (sort-by (comp-yxz))
         (partition-all 3)
         (map (fn [chateau]
                (map (fn [{[x y z] :point :as chip}]
                       (assoc chip :chip/color
                              (get (nth colors (mod (+ (dec x)
                                                       (* 3 (dec y)))
                                                    4))
                                   (mod (dec z) 3))))
                     chateau)))
         flatten)))
