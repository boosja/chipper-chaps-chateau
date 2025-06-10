(ns chipper-chaps-chateau.bot)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pick next move

(defn find-colors
  "Finds all the colors applied to the chips"
  [chips]
  (set (keep :chip/color chips)))

(defn get-point->color-map [chips]
  (-> (fn [m chip]
        (if (:chip/color chip)
          (assoc m (:point chip) (:chip/color chip))
          m))
      (reduce {} chips)))

(defn merge-wins-with-colors [wins chips]
  (let [point->color (get-point->color-map chips)]
   (mapv (fn [winning-line]
           (set (map (fn [p]
                       (if (point->color p)
                         (assoc {:point p} :chip/color (point->color p))
                         {:point p}))
                     winning-line)))
         wins)))

(defn group-by-point
  "Groups wins by point while filtering out already colored points"
  [colored-wins]
  (reduce (fn [grouped winning-line]
            (reduce #(update %1 (:point %2) conj winning-line)
                    grouped
                    (remove :chip/color winning-line)))
          {} colored-wins))

(defn get-color-frequency [wins color]
  (-> (fn [winning-line]
        (->> (map :chip/color winning-line)
             (filter #(= color %))
             count))
      (map wins)
      frequencies))

(defn statistics-by-point
  "Counts the frequency of each color per every point's winning lines
  => {[1 1 1] {:blue {0 3 1 2} ,,,} ,,,}"
  [colors pointed-wins]
  (->> pointed-wins
       (mapv (fn [[p wins]]
               [p (->> colors
                       (map (fn [clr] [clr (get-color-frequency wins clr)]))
                       (into {}))]))
       (into {})))

(def frequency->value {0 1
                       1 10
                       2 100})

(defn get-valued-score [freq n]
  (* (get frequency->value freq) n))

(defn sum-point-color-stats [sum [freq n]]
  (+ sum (get-valued-score freq n)))

(defn calc-point-scores
  "Takes the point's color frequency map and calculates its score"
  [point-stats]
  (reduce-kv (fn [res color freqs]
               (assoc res color
                      (reduce sum-point-color-stats 0 freqs)))
             {} point-stats))

(defn calc-scores [points-with-stats]
  (update-vals points-with-stats calc-point-scores))

(defn compare-point-scores [a b]
  (< (reduce max (vals b))
     (reduce max (vals a))))

(defn score-board [wins chips]
  (->> chips
       (merge-wins-with-colors wins)
       group-by-point
       (statistics-by-point (find-colors chips))
       calc-scores
       (sort-by second compare-point-scores)))

(defn pick-next-move [wins chips]
  (let [point-of-interest (ffirst (score-board wins chips))]
    (->> (filter #(= point-of-interest (:point %)) chips)
         first)))

(comment

  ;; Pick next move
  #?(:clj
     (do
       (require 'clojure.java.io)
       (def example-board
         (read-string (slurp (clojure.java.io/resource "notebooks/example-board.edn"))))
       (pick-next-move wins/d3 example-board)))
  )
