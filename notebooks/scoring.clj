^{:nextjournal.clerk/visibility {:code :hide}}
(ns scoring
  {:nextjournal.clerk/visibility {:result :show}
   :nextjournal.clerk/budget 750}
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [nextjournal.clerk :as clerk]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(do
  (defn compare-points [a b]
    (compare [(:x a) (:y a) (:z a)]
             [(:x b) (:y b) (:z b)]))

  (defn ->sorted-map [m]
    (into (sorted-map-by compare-points) m))

  (defn read-file [path]
    (read-string (slurp (io/resource (str "notebooks/" path)))))

  )

^{::clerk/visibility {:code :hide}}
(clerk/html [:h1 "Scoring the board for choosing the next move"])

;; First we have our winning scenarios. Each set in the list is a winning line
;; consisting of three points (chips) in a three-dimensional grid.
^{::clerk/auto-expand-results? false}
(def wins victory/wins)

;; Before we begin calculating the next move, we need an example board.
(def example-board (read-file "example-board.edn"))

^{::clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:width "65%"
                           :margin "0 auto"}}
             [:link {:rel "stylesheet" :href "/css/style.css"}]
             (->> (vis/el-chateau nil example-board)
                  (walk/postwalk #(if (and (vector? %) (= (first %) :g))
                                    (rest %)
                                    %)))])

;; To ease our way forward we can create a point-to-color map. This way it'll be
;; a breeze to find which point has what color.
^{::clerk/auto-expand-results? true}
(victory/->point-color-mapper example-board)

;; Then we want to merge the colored points with the winning lines defined
;; above to get a better idea where the different colors lie regards to
;; winning.
(def colored-wins (victory/merge-wins-with-colors victory/wins example-board))

;; We group the now colored winning lines by each point contained in each set of
;; winning lines.
^{::clerk/visibility {:result :hide}}
(def wins-grouped-by-point (victory/group-by-point colored-wins))
^{::clerk/visibility {:code :hide}}
(->sorted-map (victory/group-by-point colored-wins))

;; We are now able to loop through the points and for every winning line
;; containing the point calculate the frequency of each color per winning line.
;; This gives us:
^{::clerk/visibility {:result :hide}}
(def stats (victory/stats wins-grouped-by-point))
^{::clerk/visibility {:code :hide}}
(def stats (->sorted-map (victory/stats wins-grouped-by-point)))

;; Wow, fantastic!

;; Lets take a look at the first uncolored point (we are not interested in
;; points that are already colored in).
(first (filter #(-> % first :chip/color nil?) stats))

;; The result shows us the frequency of the colors in the winning lines the
;; point is a part of. Of the seven winning lines this point is a part of, six
;; does not have a single point that is `:blue`, but one has two!

;; There is one winning line with a single `:green` point, two with one `:red`
;; and no winning line has a yellow.

;; Scoring the board points are now possible, but before we do we need to put
;; values to the different frequencies of colors for each point. For instance,
;; if a winning line point is colored blue and the rest have no color, how
;; should we interpret this?

;; I have concocted this value set:

;; Winning lines containing current color:
;; 0 times are valued as 1
;; 1 times are valued as 2
;; 2 times are valued as 10
^{::clerk/visibility {:code :hide}}
(clerk/html [:div.flex.justify-center
             (clerk/table {"frequency" [0 1 2]
                           "valued as" [1 2 10]})])

;; So to calculate the score of a point with the winning line frequency map `{0 6,
;; 2 1}`, we can do:

;; ```
;; (* 1 6) => 6
;; (* 10 1) => 10
;; = 16
;; ```

(reduce (fn [sum [freq n]]
          (+ sum (* (get {0 1, 1 2, 2 10} freq) n)))
        0 {0 6, 2 1})

;; (+ (* 6 1)   ;; 0 are valued as 1
;;    (* 1 10)) ;; 2 are valued as 10
;; => 10
