(ns chipper-chaps-chateau.bot
  (:require [chipper-chaps-chateau.wins :as wins]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.board :as board]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.player :as player]))

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

(defn get-color-candidates [candidates color]
  (filter #(< 100 (color (second %))) candidates))

(defn by-player-order [score-board current-color]
  (let [candidates (take-while #(< 100 (apply max (-> % second vals)))
                               score-board)]
    (if (seq candidates)
     (loop [color current-color]
       (if-let [final-canditates (seq (get-color-candidates candidates color))]
         final-canditates
         (if (= (player/next color) current-color)
           score-board
           (recur (player/next color)))))
     score-board)))

(defn pick-next-move [wins chips current-color]
  (let [score-board (score-board wins chips)
        point-of-interest (ffirst (by-player-order score-board current-color))]
    (->> (filter #(= point-of-interest (:point %)) chips)
         first)))

(comment

  (do
    (def wins wins/d3)
    (def chips chips/example-board)
    (def colors (find-colors chips))
    (def colored-wins (merge-wins-with-colors wins chips))
    (def pointed-wins (group-by-point colored-wins))
    (def stats-by-point (statistics-by-point colors pointed-wins))
    (def scores (calc-scores stats-by-point))
    (def sorted (sort-by second compare-point-scores scores))
    (def checked-player-order (by-player-order sorted :blue))

    )

  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bot actions

(defn deferred-bot-move-effects [db ms]
  (let [settings (db/settings db)
        amount (cond-> 0
                 (:settings/enable-bot settings) inc
                 (and (:settings/enable-bot settings)
                      (= :four-player (:settings/variant settings))) (+ 2))]
    (remove nil? [(when (< 0 amount)
                    [:effect/defer ms (into [] (repeat amount [:bot/move]))])])))

(def ->wins {27 wins/d3
             81 wins/d4
             243 wins/d5})

(defn bot-move-effects [db]
  (let [game (db/current-game db)
        current-color (:game/current-color game)
        chips (:game/chips game)
        wins (->wins (count chips))]
    (if (victory/has-winner? chips wins)
      []
      (let [next-move (pick-next-move wins chips current-color)]
        (board/select-chip db [next-move])))))

(defn perform-action [db [action & args]]
  (case action
    :bot/deferred-move (deferred-bot-move-effects db (first args))
    :bot/move (bot-move-effects db)
    nil))

(comment

  ;; Pick next move
  #?(:clj
     (do
       (require 'clojure.java.io)
       (def example-board
         (read-string (slurp (clojure.java.io/resource "notebooks/example-board.edn"))))
       (pick-next-move wins/d3 example-board)))
  )
