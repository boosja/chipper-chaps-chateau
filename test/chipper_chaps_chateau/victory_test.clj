(ns chipper-chaps-chateau.victory-test
  (:require [chipper-chaps-chateau.bot :as bot]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.wins :as wins]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

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
    (is (= (distinct (map #(victory/has-winner?
                            (chips/add-winning-line (chips/create-chips :dim/three) %)
                            wins/d3)
                          wins/d3))
           '(:blue))))

  (testing "returns :tie on a tie"
    (is (= (victory/has-winner? tied-chips wins/d3)
           :tie)))

  (testing "returns :blue when winning on last chip"
    (is (= (victory/has-winner? (map #(assoc % :chip/color :blue)
                                          (chips/create-chips :dim/three))
                                wins/d3)
           :blue)))

  (testing "returns false when no one has won yet"
    (is (= (victory/has-winner? (chips/create-chips :dim/three) wins/d3)
           false))))

(comment

  (map #(victory/has-winner?
         (chips/add-winning-line (chips/create-chips :dim/three) %)
         wins/d3)
       wins/d3)

  )

(def board-with-colors '({:point [1 1 1] :chip/color :blue}
                         {:point [1 1 2] :chip/color :blue}
                         {:point [1 1 3]}
                         {:point [2 1 1] :chip/color :green}
                         {:point [2 1 2]}
                         {:point [2 1 3]}
                         {:point [3 1 1]}
                         {:point [3 1 2]}
                         {:point [3 1 3]}
                         {:point [1 2 1]}
                         {:point [1 2 2] :chip/color :red}
                         {:point [1 2 3]}
                         {:point [2 2 1]}
                         {:point [2 2 2] :chip/color :green}
                         {:point [2 2 3] :chip/color :red}
                         {:point [3 2 1] :chip/color :blue}
                         {:point [3 2 2]}
                         {:point [3 2 3]}
                         {:point [1 3 1]}
                         {:point [1 3 2]}
                         {:point [1 3 3]}
                         {:point [2 3 1]}
                         {:point [2 3 2] :chip/color :yellow}
                         {:point [2 3 3]}
                         {:point [3 3 1]}
                         {:point [3 3 2] :chip/color :yellow}
                         {:point [3 3 3]}))

(deftest score-moves
  (testing "Picks first possible move"
    (is (not
         (contains? (bot/pick-next-move
                     wins/d3
                     (chips/add-winning-line (chips/create-chips :dim/three)
                                             #{[1 1 1]}))
                    :chip/color))))

  (testing "Picks first possible move of other color"
    (is (not
         (= (bot/pick-next-move wins/d3
                                (chips/add-winning-line (chips/create-chips :dim/three)
                                                        #{[1 1 1]}))
            :chip/color))))

  (testing "Not nil"
    (is (not (nil? (bot/pick-next-move
                    wins/d3
                    (chips/add-winning-line (chips/create-chips :dim/three) #{[1 1 1]}))))))

  ;; - (attack) Pick chip in a win that has my color already
  ;; - (attack) Pick chip in a win that has my color already and does not have any
  ;; other colors
  ;; - (defence) Pick chip in a win that has two of the same color that is not
  ;; mine
  ;; - (defence) Pick chip in a win that has two of the same color that is not
  ;; mine and can win right after me

  )

(deftest ->point-color-mapper-test
  (is (= (bot/->point-color-mapper [{:point [1 1 1] :chip/color :blue}
                                    {:point [1 1 2]}
                                    {:point [1 1 3] :chip/color :green}
                                    {:point [2 1 1] :chip/color :blue}
                                    {:point [2 1 2]}
                                    {:point [2 1 3]}
                                    {:point [3 1 1]}
                                    {:point [3 1 2] :chip/color :red}
                                    {:point [3 1 3]}
                                    {:point [1 2 1]}])
         {[1 1 1] :blue
          [1 1 3] :green
          [2 1 1] :blue
          [3 1 2] :red})))

(deftest merge-wins-with-colors-test
  (is (= (bot/merge-wins-with-colors wins/d3 board-with-colors)
         (read-string (slurp (io/resource "test-data/colored-wins.edn"))))))

(deftest group-by-point-test
  (is (= (bot/group-by-point
          (bot/merge-wins-with-colors wins/d3 board-with-colors))
         (read-string (slurp (io/resource "test-data/wins-grouped-by-point.edn")))))

  ;; validate the keys are actually in every winning-line assigned to it
  )

(comment

  ;; sorted wins grouped by point
  (sort-by #(contains? (first %) :chip/color)
           (bot/group-by-point
            (bot/merge-wins-with-colors wins/d3 board-with-colors)))


  (def stats (bot/stats (bot/group-by-point
                         (bot/merge-wins-with-colors wins/d3 board-with-colors))
                        ))

  (->> stats
       (filter #(-> % first :chip/color nil?))
       bot/calc-scores
       (sort-by second victory/compare-point-scores))

  (->> (chips/add-winning-line (chips/create-chips :dim/three) #{{:x 1 :y 1 :z 1}})
       (filter :chip/color))


  )
