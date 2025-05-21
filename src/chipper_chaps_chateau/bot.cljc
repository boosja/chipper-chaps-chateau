(ns chipper-chaps-chateau.bot)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pick next move

(defn ->point-color-mapper [chips]
  (-> (fn [m chip]
        (if (:chip/color chip)
          (assoc m (:point chip) (:chip/color chip))
          m))
      (reduce {} chips)))

(defn merge-wins-with-colors [wins chips]
  (let [point-color-mapper (->point-color-mapper chips)]
   (mapv (fn [winning-line]
           (-> (fn [p]
                 (if (get point-color-mapper p)
                   (assoc {:point p} :chip/color (get point-color-mapper p))
                   {:point p}))
               (map winning-line)
               set))
         wins)))

(defn group-by-point [colored-wins]
  (-> (fn [grouped winning-line]
        (let [ps (vec winning-line)]
          (-> grouped
              (update (nth ps 0) #(conj % winning-line))
              (update (nth ps 1) #(conj % winning-line))
              (update (nth ps 2) #(conj % winning-line)))))
      (reduce {} colored-wins)))

(defn get-color-stats [wins color]
  (-> (fn [winning-line]
        (->> (map :chip/color winning-line)
             (filter #(= color %))
             count))
      (map wins)
      frequencies))

(defn stats [pointed-wins]
  (->> pointed-wins
       (mapv (fn [[p wins]]
               [p {:blue (get-color-stats wins :blue)
                   :red (get-color-stats wins :red)
                   :green (get-color-stats wins :green)
                   :yellow (get-color-stats wins :yellow)}]))
       (into {})))

(def frequency->value {0 1
                       1 10
                       2 100})

(defn get-valued-score [freq n]
  (* (get frequency->value freq) n))

(defn sum-point-color-stats [sum [freq n]]
  (+ sum (get-valued-score freq n)))

(defn calc-point-scores [point-stats]
  (reduce-kv (fn [res k v]
               (assoc res k
                      (reduce sum-point-color-stats 0 v)))
             {}
             point-stats))

(defn compare-point-scores [a b]
  (< (reduce max (vals b))
     (reduce max (vals a))))

(defn calc-scores [points-with-stats]
  (map (fn [[point point-stats]]
         [point (calc-point-scores point-stats)])
       points-with-stats))

(defn pick-next-move [wins chips]
  (let [next-move (->> chips
                       (merge-wins-with-colors wins)
                       group-by-point
                       stats
                       (filter #(nil? (-> % first :chip/color)))
                       calc-scores
                       (sort-by second compare-point-scores)
                       ffirst
                       :point)]
    (first (filter #(= next-move (:point %)) chips))))

(comment

  ;; Pick next move
  #?(:clj
     (do
       (require 'clojure.java.io)
       (def example-board
         (read-string (slurp (clojure.java.io/resource "notebooks/example-board.edn"))))
       (pick-next-move wins/d3 example-board)))
  )
