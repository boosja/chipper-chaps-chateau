(ns chipper-chaps-chateau.victory
  (:require [clojure.set :as set]
            [chipper-chaps-chateau.chips :as chips]))

(def wins [ ;; horizontal (rows)
           #{{:y 1, :x 1, :z 1} {:y 1, :x 2, :z 1} {:y 1, :x 3, :z 1}}
           #{{:y 2, :x 1, :z 1} {:y 2, :x 2, :z 1} {:y 2, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 1} {:y 3, :x 2, :z 1} {:y 3, :x 3, :z 1}}
           #{{:y 1, :x 1, :z 2} {:y 1, :x 2, :z 2} {:y 1, :x 3, :z 2}}
           #{{:y 2, :x 1, :z 2} {:y 2, :x 2, :z 2} {:y 2, :x 3, :z 2}}
           #{{:y 3, :x 1, :z 2} {:y 3, :x 2, :z 2} {:y 3, :x 3, :z 2}}
           #{{:y 1, :x 1, :z 3} {:y 1, :x 2, :z 3} {:y 1, :x 3, :z 3}}
           #{{:y 2, :x 1, :z 3} {:y 2, :x 2, :z 3} {:y 2, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 3} {:y 3, :x 2, :z 3} {:y 3, :x 3, :z 3}}
           ;; vertical (cols)
           #{{:y 1, :x 1, :z 1} {:y 2, :x 1, :z 1} {:y 3, :x 1, :z 1}}
           #{{:y 1, :x 2, :z 1} {:y 2, :x 2, :z 1} {:y 3, :x 2, :z 1}}
           #{{:y 1, :x 3, :z 1} {:y 2, :x 3, :z 1} {:y 3, :x 3, :z 1}}
           #{{:y 1, :x 1, :z 2} {:y 2, :x 1, :z 2} {:y 3, :x 1, :z 2}}
           #{{:y 1, :x 2, :z 2} {:y 2, :x 2, :z 2} {:y 3, :x 2, :z 2}}
           #{{:y 1, :x 3, :z 2} {:y 2, :x 3, :z 2} {:y 3, :x 3, :z 2}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 1, :z 3} {:y 3, :x 1, :z 3}}
           #{{:y 1, :x 2, :z 3} {:y 2, :x 2, :z 3} {:y 3, :x 2, :z 3}}
           #{{:y 1, :x 3, :z 3} {:y 2, :x 3, :z 3} {:y 3, :x 3, :z 3}}
           ;; depth (z)
           #{{:y 1, :x 1, :z 1} {:y 1, :x 1, :z 2} {:y 1, :x 1, :z 3}}
           #{{:y 1, :x 2, :z 1} {:y 1, :x 2, :z 2} {:y 1, :x 2, :z 3}}
           #{{:y 1, :x 3, :z 1} {:y 1, :x 3, :z 2} {:y 1, :x 3, :z 3}}
           #{{:y 2, :x 1, :z 1} {:y 2, :x 1, :z 2} {:y 2, :x 1, :z 3}}
           #{{:y 2, :x 2, :z 1} {:y 2, :x 2, :z 2} {:y 2, :x 2, :z 3}}
           #{{:y 2, :x 3, :z 1} {:y 2, :x 3, :z 2} {:y 2, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 1} {:y 3, :x 1, :z 2} {:y 3, :x 1, :z 3}}
           #{{:y 3, :x 2, :z 1} {:y 3, :x 2, :z 2} {:y 3, :x 2, :z 3}}
           #{{:y 3, :x 3, :z 1} {:y 3, :x 3, :z 2} {:y 3, :x 3, :z 3}}
           ;; diagonal
           #{{:y 1, :x 1, :z 1} {:y 2, :x 2, :z 1} {:y 3, :x 3, :z 1}}
           #{{:y 1, :x 1, :z 2} {:y 2, :x 2, :z 2} {:y 3, :x 3, :z 2}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 2, :z 3} {:y 3, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 1} {:y 2, :x 2, :z 1} {:y 1, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 2} {:y 2, :x 2, :z 2} {:y 1, :x 3, :z 2}}
           #{{:y 3, :x 1, :z 3} {:y 2, :x 2, :z 3} {:y 1, :x 3, :z 3}}
           ;; depth horizontal diagonal (rows)
           #{{:y 1, :x 1, :z 1} {:y 1, :x 2, :z 2} {:y 1, :x 3, :z 3}}
           #{{:y 2, :x 1, :z 1} {:y 2, :x 2, :z 2} {:y 2, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 1} {:y 3, :x 2, :z 2} {:y 3, :x 3, :z 3}}
           #{{:y 1, :x 1, :z 3} {:y 1, :x 2, :z 2} {:y 1, :x 3, :z 1}}
           #{{:y 2, :x 1, :z 3} {:y 2, :x 2, :z 2} {:y 2, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 3} {:y 3, :x 2, :z 2} {:y 3, :x 3, :z 1}}
           ;; depth verical diagonal (cols)
           #{{:y 1, :x 1, :z 1} {:y 2, :x 1, :z 2} {:y 3, :x 1, :z 3}}
           #{{:y 1, :x 2, :z 1} {:y 2, :x 2, :z 2} {:y 3, :x 2, :z 3}}
           #{{:y 1, :x 3, :z 1} {:y 2, :x 3, :z 2} {:y 3, :x 3, :z 3}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 1, :z 2} {:y 3, :x 1, :z 1}}
           #{{:y 1, :x 2, :z 3} {:y 2, :x 2, :z 2} {:y 3, :x 2, :z 1}}
           #{{:y 1, :x 3, :z 3} {:y 2, :x 3, :z 2} {:y 3, :x 3, :z 1}}
           ;; depth diagonal diagonal
           #{{:y 1, :x 1, :z 1} {:y 2, :x 2, :z 2} {:y 3, :x 3, :z 3}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 2, :z 2} {:y 3, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 1} {:y 2, :x 2, :z 2} {:y 1, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 3} {:y 2, :x 2, :z 2} {:y 1, :x 3, :z 1}}
           ])

(defn has-point? [chips xyz]
  (some-> (filter #(= (chips/->xyz %) xyz) chips)
          first))

(defn insert-chips [winning-lines chips]
  (map (fn [winning-line]
         (set (map #(or (has-point? chips %) %)
                   winning-line)))
       winning-lines))

(defn has-color? [winning-line color]
  (some #(= (get % :chip/color) color) winning-line))

(defn count-color [winning-line color]
  (count (filter #(= (get % :chip/color) color)
                 winning-line)))

(comment

  ;; chips that have most winning opportunities (heatmap)
  (->> (chips/create-chips)
       (map (fn [c]
              [(count (filter #(contains? % (select-keys c [:y :x :z])) wins)) c]))
       (map #(assoc (second %) :chip/color (get {13 :red
                                                 7 :yellow
                                                 5 :green
                                                 4 :blue}
                                                (first %)))))

  ;; frequency of point in winning lines
  (count (filter #(contains? % {:y 1 :x 1 :z 1}) wins))

  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pick next move

(defn ->point-color-mapper [chips]
  (-> (fn [m chip]
        (if (:chip/color chip)
          (assoc m (chips/->xyz chip) (:chip/color chip))
          m))
      (reduce {} chips)))

(defn merge-wins-with-colors [wins chips]
  (let [point-color-mapper (->point-color-mapper chips)]
   (mapv (fn [winning-line]
           (-> (fn [p]
                 (if (get point-color-mapper p)
                   (assoc p :chip/color (get point-color-mapper p))
                   p))
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

(defn collapse [color stats]
  (->> stats
       (filter #(-> % first :chip/color nil?))
       (map (fn [[p p-stats]]
              [p (->> p-stats color
                      (reduce (fn [s [freq n]] (+ s (get-valued-score freq n))) 0))]))
       (sort-by second)
       reverse
       ffirst))

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

(defn pick-next-move [wins chips color]
  (let [next-move (->> chips
                       (merge-wins-with-colors wins)
                       group-by-point
                       stats
                       (filter #(nil? (-> % first :chip/color)))
                       calc-scores
                       (sort-by second compare-point-scores)
                       ffirst)]
    (first (filter #(= next-move (chips/->xyz %))
                   chips))))

(comment

  ;; Pick next move
  #?(:clj
     (do
       (require 'clojure.java.io)
       (def example-board
         (read-string (slurp (clojure.java.io/resource "notebooks/example-board.edn"))))
       (pick-next-move wins example-board :blue)))
  )

(defn vals->sets [m]
  (update-vals m (fn [ps]
                   (set (map #(select-keys % [:x :y :z]) ps)))))

(defn has-three-in-a-row? [chips]
  (some #(set/subset? % chips) wins))

(defn did-someone-win? [chips]
  (let [filtered (filter :chip/color chips)
        grouped (->> filtered
                     (group-by :chip/color)
                     (vals->sets))
        winner (reduce (fn [winner [color chips]]
                               (if (has-three-in-a-row? chips)
                                 color
                                 winner))
                             false
                             grouped)]
    (cond
      winner
      winner

      (= (count chips) (count filtered))
      :tie

      :else
      false)))
