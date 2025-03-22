(ns chipper-chaps-chateau.chips
  (:require [chipper-chaps-chateau.victory :as victory]))

(defn create-chips []
  (for [i (range 3)
        j (range 3)
        k (range 3)]
    (let [x (inc j)
          y (inc i)
          z (inc k)]
      {:x x
       :y y
       :z z
       :chip/size (get {1 :lg 2 :md 3 :sm} z)
       :chip/idx (+ (* 100 y) (* 10 x) (* 1 z))})))

(defn ->cells [chips get-actions]
  (->> chips
       (partition 3)
       (mapv
        #(mapv (fn [{:keys [chip/size chip/color] :as chip}]
                 {:size size
                  :color color
                  :actions (get-actions chip)})
               %))))

(defn is-in? [winning-line chip]
  (contains? winning-line (select-keys chip [:x :y :z])))

(defn replace-with [chips winning-line]
  (mapv (fn [c]
          (if (is-in? winning-line c)
            (assoc c :chip/color :blue)
            c))
        chips))

(comment
  (def chips
    (create-chips))

  (def winning-line
    (first victory/wins))

  (mapv (fn [c]
          (if (is-in? winning-line c)
            (assoc c :chip/color :blue)
            c))
        chips)

  )
