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
     :svg/circle (get svg-circles (str y x z))
     :chip/idx (+ (* 100 y) (* 10 x) (* 1 z))}))

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
