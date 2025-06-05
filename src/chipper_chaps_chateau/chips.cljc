(ns chipper-chaps-chateau.chips)

(defn ->xyz [chip]
  (select-keys chip [:x :y :z :w :v :u :t]))

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

(defn create-chips-1d []
  (for [x (range 1 4)]
    {:point [x]
     :svg/circle (get svg-circles (str "2" x "1"))}))

(defn create-chips-2d []
  (for [y (range 1 4)
        x (range 1 4)]
    {:point [x y]
     :svg/circle (get svg-circles (str y x "1"))}))

(defn create-chips-3d []
  (for [y (range 1 4)
        x (range 1 4)
        z (range 1 4)]
    {:point [x y z]
     :svg/circle (get svg-circles (str y x z))}))

(defn create-chips-4d []
  (for [w (range 1 4)
        y (range 1 4)
        x (range 1 4)
        z (range 1 4)]
    {:point [x y z w]
     :svg/circle (get svg-circles (str y x z))}))

(defn create-chips-5d []
  (for [v (range 1 4)
        w (range 1 4)
        y (range 1 4)
        x (range 1 4)
        z (range 1 4)]
    {:point [x y z w v]
     :svg/circle (get svg-circles (str y x z))}))

(comment
  (->> (create-chips-3d) (map :point))
  (->> (create-chips-4d) (map ->xyz))
  (->> (create-chips-5d) (map ->xyz))
  )

(defn add-winning-line
  "Colors the winning line in the list of chips"
  [chips winning-line & [color]]
  (mapv (fn [chip]
          (if (some #{(:point chip)} winning-line)
            (assoc chip :chip/color (or color :blue))
            chip))
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
  (->> (create-chips-3d)
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
    (->> (create-chips-3d)
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
    (->> (create-chips-3d)
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
