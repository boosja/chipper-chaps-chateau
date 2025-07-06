^{:nextjournal.clerk/visibility {:code :hide}}
(ns scoring
  {:nextjournal.clerk/visibility {:result :show}
   :nextjournal.clerk/budget 750}
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.wins :as wins]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [nextjournal.clerk :as clerk]))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(do
  (defn compare-points [a b]
    (compare (:point a) (:point b)))

  (defn ->sorted-map [m]
    (into (sorted-map-by compare-points) m))

  (defn read-file [path]
    (read-string (slurp (io/resource (str "notebooks/" path)))))

  )

^{::clerk/visibility {:code :hide}}
(clerk/html [:h1 "Scoring the board for choosing the next move"])

^{::clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:color "black"
                           :background "yellow"
                           :padding "1rem"
                           :text-align "center"
                           :border "1px solid black"}}
             "WIP - WORK IN PROGRESS - WIP"])

;; First we have our winning scenarios. Each set in the list is a winning line
;; consisting of three points (chips) in a three-dimensional grid.
^{::clerk/auto-expand-results? false}
(def wins wins/d3)

;; Before we begin calculating the next move, we need an example board.
(def example-board (read-file "example-board.edn"))

^{::clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:width "65%"
                           :margin "0 auto"}}
             [:link {:rel "stylesheet" :href "/css/style.css"}]
             (->> [::vis/board.board
                   {::vis/chips example-board}
                   [:circle.shadow]
                   [:circle]]
                  (walk/postwalk #(if (and (vector? %) (= (first %) :g))
                                    (rest %)
                                    %)))])

;; To ease our way forward we can merge the board points into the winning lines
;; defined above to get a better idea where the different colors lie in regard
;; to winning.
(def colored-wins (victory/merge-wins-with-colors wins/d3 example-board))

;; Let's group them by point:
^{::clerk/visibility {:result :hide}}
(def wins-grouped-by-point (victory/group-by-point colored-wins))
^{::clerk/visibility {:code :hide}}
(->sorted-map (victory/group-by-point colored-wins))

;; We are now able to loop through the points, and for every winning line
;; containing the point, calculate the frequency of each color.

;; This gives us:
^{::clerk/visibility {:result :hide}}
(def stats (victory/stats wins-grouped-by-point))
^{::clerk/visibility {:code :hide}}
(def stats (->sorted-map (victory/stats wins-grouped-by-point)))

;; Wow, fantastic!

;; Lets take a look at the first uncolored point (we are not interested in
;; points that are already colored in).
(def point (first (filter #(-> % first :chip/color nil?) stats)))

;; The result shows us the frequency of the colors in the winning lines the
;; point is a part of.

;; Of the four winning lines this point is a part of, three
;; does not have a single point that is `:blue`, but one does!

;; There is one winning line with a single red point, one with a single
;; yellow point, no winning line has a green.

;; Scoring the board points are now possible, but before we do we need to put
;; values to the different frequencies of colors for each point. For instance,
;; if a winning line point is colored blue and the rest have no color, how
;; should we interpret this?

;; I have concocted this value set:

;; Winning lines containing current color:
;; 0 times are valued as 1
;; 1 times are valued as 10
;; 2 times are valued as 100
^{::clerk/visibility {:code :hide}}
(clerk/html [:div.flex.justify-center
             (clerk/table {"frequency" [0 1 2]
                           "valued as" [1 10 100]})])

;; So to calculate the score of a point for color blue
(def blue (:blue (second point)))
;; we can do:

(+ (* 1 3)
   (* 10 1))

(reduce (fn [s [freq n]]
          (+ s (* (get {0 1, 1 10, 2 100} freq) n)))
        0
        blue)

;; For all points and colors:
(->> (filter #(nil? (-> % first :chip/color)) stats)
     victory/calc-scores
     (sort-by second victory/compare-point-scores))
