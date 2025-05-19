(ns chipper-chaps-chateau.victory-test
  (:require [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.wins :as wins]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest vals->sets-test
  (testing "The vals of the map are converted to sets"
    (is (= (victory/vals->sets {:blue [{:x 1 :y 1 :z 1}
                                       {:x 2 :y 2 :z 2}
                                       {:x 3 :y 3 :z 3}]})
           {:blue #{{:x 1 :y 1 :z 1}
                    {:x 2 :y 2 :z 2}
                    {:x 3 :y 3 :z 3}}}))))

(def tied-chips '({:x 1, :y 1, :z 1, :chip/color :blue, :chip/id 111}
                  {:x 1, :y 1, :z 2, :chip/color :blue, :chip/id 112}
                  {:x 1, :y 1, :z 3, :chip/color :yellow, :chip/id 113}
                  {:x 2, :y 1, :z 1, :chip/color :blue, :chip/id 121}
                  {:x 2, :y 1, :z 2, :chip/color :blue, :chip/id 122}
                  {:x 2, :y 1, :z 3, :chip/color :yellow, :chip/id 123}
                  {:x 3, :y 1, :z 1, :chip/color :yellow, :chip/id 131}
                  {:x 3, :y 1, :z 2, :chip/color :yellow, :chip/id 132}
                  {:x 3, :y 1, :z 3, :chip/color :green, :chip/id 133}
                  {:x 1, :y 2, :z 1, :chip/color :red, :chip/id 211}
                  {:x 1, :y 2, :z 2, :chip/color :red, :chip/id 212}
                  {:x 1, :y 2, :z 3, :chip/color :blue, :chip/id 213}
                  {:x 2, :y 2, :z 1, :chip/color :blue, :chip/id 221}
                  {:x 2, :y 2, :z 2, :chip/color :red, :chip/id 222}
                  {:x 2, :y 2, :z 3, :chip/color :green, :chip/id 223}
                  {:x 3, :y 2, :z 1, :chip/color :yellow, :chip/id 231}
                  {:x 3, :y 2, :z 2, :chip/color :yellow, :chip/id 232}
                  {:x 3, :y 2, :z 3, :chip/color :green, :chip/id 233}
                  {:x 1, :y 3, :z 1, :chip/color :red, :chip/id 311}
                  {:x 1, :y 3, :z 2, :chip/color :red, :chip/id 312}
                  {:x 1, :y 3, :z 3, :chip/color :blue, :chip/id 313}
                  {:x 2, :y 3, :z 1, :chip/color :green, :chip/id 321}
                  {:x 2, :y 3, :z 2, :chip/color :green, :chip/id 322}
                  {:x 2, :y 3, :z 3, :chip/color :red, :chip/id 323}
                  {:x 3, :y 3, :z 1, :chip/color :green, :chip/id 331}
                  {:x 3, :y 3, :z 2, :chip/color :green, :chip/id 332}
                  {:x 3, :y 3, :z 3, :chip/color :red, :chip/id 333}))

(deftest did-someone-win?-test
  (testing "all winning lines return :blue"
    (is (= (distinct (map #(victory/did-someone-win?
                            (chips/add-winning-line (chips/create-chips) %))
                          wins/d3))
           '(:blue))))

  (testing "returns :tie on a tie"
    (is (= (victory/did-someone-win? tied-chips)
           :tie)))

  (testing "returns :blue when winning on last chip"
    (is (= (victory/did-someone-win? (map #(assoc % :chip/color :blue)
                                          (chips/create-chips)))
           :blue)))

  (testing "returns false when no one has won yet"
    (is (= (victory/did-someone-win? (chips/create-chips))
           false))))

(comment

  (map #(victory/did-someone-win?
         (chips/add-winning-line (chips/create-chips) %))
       wins/d3)

  )

(def board-with-colors '({:x 1 :y 1 :z 1 :chip/color :blue}
                         {:x 1 :y 1 :z 2 :chip/color :blue}
                         {:x 1 :y 1 :z 3}
                         {:x 2 :y 1 :z 1 :chip/color :green}
                         {:x 2 :y 1 :z 2}
                         {:x 2 :y 1 :z 3}
                         {:x 3 :y 1 :z 1}
                         {:x 3 :y 1 :z 2}
                         {:x 3 :y 1 :z 3}
                         {:x 1 :y 2 :z 1}
                         {:x 1 :y 2 :z 2 :chip/color :red}
                         {:x 1 :y 2 :z 3}
                         {:x 2 :y 2 :z 1}
                         {:x 2 :y 2 :z 2 :chip/color :green}
                         {:x 2 :y 2 :z 3 :chip/color :red}
                         {:x 3 :y 2 :z 1 :chip/color :blue}
                         {:x 3 :y 2 :z 2}
                         {:x 3 :y 2 :z 3}
                         {:x 1 :y 3 :z 1}
                         {:x 1 :y 3 :z 2}
                         {:x 1 :y 3 :z 3}
                         {:x 2 :y 3 :z 1}
                         {:x 2 :y 3 :z 2 :chip/color :yellow}
                         {:x 2 :y 3 :z 3}
                         {:x 3 :y 3 :z 1}
                         {:x 3 :y 3 :z 2 :chip/color :yellow}
                         {:x 3 :y 3 :z 3}))

(comment
  (def current-color :blue)
  (def filled-in (filter :chip/color board-with-colors))

  (->> (map (fn [winning-line]
              (set (map (fn [winning-xyz]
                          (or (some-> (filter #(= (chips/->xyz %) winning-xyz) filled-in)
                                      first)
                              winning-xyz))
                        winning-line)))
            wins/d3)
       (filter (fn [winning-line]
                 (some #(= (get % :chip/color) current-color) winning-line)))
       (sort-by (fn [winning-line]
                  (count (filter #(= (get % :chip/color) current-color)
                                 winning-line))))
       (reverse))

  (->> (victory/insert-chips wins/d3 filled-in)
       (filter #(victory/has-color? % current-color))
       (sort-by #(victory/count-color % current-color))
       reverse)

  )

(deftest score-moves
  (testing "Picks first possible move"
    (is (not
         (contains? (victory/pick-next-move wins/d3
                                            (chips/add-winning-line (chips/create-chips)
                                                                #{{:x 1 :y 1 :z 1}}))
                    :chip/color))))

  (testing "Picks first possible move of other color"
    (is (not
         (= (victory/pick-next-move wins/d3
                                    (chips/add-winning-line (chips/create-chips)
                                                        #{{:x 1 :y 1 :z 1}}))
            :chip/color))))

  (testing "Not nil"
    (is (not (nil? (victory/pick-next-move wins/d3
                                           (chips/add-winning-line (chips/create-chips)
                                                               #{{:x 1 :y 1 :z 1}}))))))

  ;; - (attack) Pick chip in a win that has my color already
  ;; - (attack) Pick chip in a win that has my color already and does not have any
  ;; other colors
  ;; - (defence) Pick chip in a win that has two of the same color that is not
  ;; mine
  ;; - (defence) Pick chip in a win that has two of the same color that is not
  ;; mine and can win right after me

  )

(deftest ->point-color-mapper-test
  (is (= (victory/->point-color-mapper [{:x 1, :y 1, :z 1 :chip/color :blue}
                                        {:x 1, :y 1, :z 2}
                                        {:x 1, :y 1, :z 3 :chip/color :green}
                                        {:x 2, :y 1, :z 1 :chip/color :blue}
                                        {:x 2, :y 1, :z 2}
                                        {:x 2, :y 1, :z 3}
                                        {:x 3, :y 1, :z 1}
                                        {:x 3, :y 1, :z 2 :chip/color :red}
                                        {:x 3, :y 1, :z 3}
                                        {:x 1, :y 2, :z 1}])
         {{:x 1, :y 1, :z 1} :blue
          {:x 1, :y 1, :z 3} :green
          {:x 2, :y 1, :z 1} :blue
          {:x 3, :y 1, :z 2} :red})))

(deftest merge-wins-with-colors-test
  (is (= (victory/merge-wins-with-colors wins/d3 board-with-colors)
         (read-string (slurp (io/resource "test-data/colored-wins.edn"))))))

(deftest group-by-point-test
  (is (= (victory/group-by-point
          (victory/merge-wins-with-colors wins/d3 board-with-colors))
         (read-string (slurp (io/resource "test-data/wins-grouped-by-point.edn")))))

  ;; validate the keys are actually in every winning-line assigned to it
  )

(comment

  ;; sorted wins grouped by point
  (sort-by #(contains? (first %) :chip/color)
           (victory/group-by-point
            (victory/merge-wins-with-colors wins/d3 board-with-colors)))


  (def stats (victory/stats (victory/group-by-point
                             (victory/merge-wins-with-colors wins/d3 board-with-colors))
                            ))

  (->> stats
       (filter #(-> % first :chip/color nil?))
       victory/calc-scores
       (sort-by second victory/compare-point-scores))

  (->> (chips/add-winning-line (chips/create-chips) #{{:x 1 :y 1 :z 1}})
       (filter :chip/color))


  )
