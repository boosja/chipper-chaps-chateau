(ns chipper-chaps-chateau.victory-test
  (:require [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.victory :as victory]
            [clojure.test :refer [deftest is testing]]))

(deftest vals->sets-test
  (testing "The vals of the map are converted to sets"
    (is (= (victory/vals->sets {:blue [{:x 1 :y 1 :z 1}
                                       {:x 2 :y 2 :z 2}
                                       {:x 3 :y 3 :z 3}]})
           {:blue #{{:x 1 :y 1 :z 1}
                    {:x 2 :y 2 :z 2}
                    {:x 3 :y 3 :z 3}}}))))

(def tied-chips '({:x 1, :y 1, :z 1, :chip/color :blue, :chip/idx 111, :chip/size :lg}
                  {:x 1, :y 1, :z 2, :chip/color :blue, :chip/idx 112, :chip/size :md}
                  {:x 1, :y 1, :z 3, :chip/color :yellow, :chip/idx 113, :chip/size :sm}
                  {:x 2, :y 1, :z 1, :chip/color :blue, :chip/idx 121, :chip/size :lg}
                  {:x 2, :y 1, :z 2, :chip/color :blue, :chip/idx 122, :chip/size :md}
                  {:x 2, :y 1, :z 3, :chip/color :yellow, :chip/idx 123, :chip/size :sm}
                  {:x 3, :y 1, :z 1, :chip/color :yellow, :chip/idx 131, :chip/size :lg}
                  {:x 3, :y 1, :z 2, :chip/color :yellow, :chip/idx 132, :chip/size :md}
                  {:x 3, :y 1, :z 3, :chip/color :green, :chip/idx 133, :chip/size :sm}
                  {:x 1, :y 2, :z 1, :chip/color :red, :chip/idx 211, :chip/size :lg}
                  {:x 1, :y 2, :z 2, :chip/color :red, :chip/idx 212, :chip/size :md}
                  {:x 1, :y 2, :z 3, :chip/color :blue, :chip/idx 213, :chip/size :sm}
                  {:x 2, :y 2, :z 1, :chip/color :blue, :chip/idx 221, :chip/size :lg}
                  {:x 2, :y 2, :z 2, :chip/color :red, :chip/idx 222, :chip/size :md}
                  {:x 2, :y 2, :z 3, :chip/color :green, :chip/idx 223, :chip/size :sm}
                  {:x 3, :y 2, :z 1, :chip/color :yellow, :chip/idx 231, :chip/size :lg}
                  {:x 3, :y 2, :z 2, :chip/color :yellow, :chip/idx 232, :chip/size :md}
                  {:x 3, :y 2, :z 3, :chip/color :green, :chip/idx 233, :chip/size :sm}
                  {:x 1, :y 3, :z 1, :chip/color :red, :chip/idx 311, :chip/size :lg}
                  {:x 1, :y 3, :z 2, :chip/color :red, :chip/idx 312, :chip/size :md}
                  {:x 1, :y 3, :z 3, :chip/color :blue, :chip/idx 313, :chip/size :sm}
                  {:x 2, :y 3, :z 1, :chip/color :green, :chip/idx 321, :chip/size :lg}
                  {:x 2, :y 3, :z 2, :chip/color :green, :chip/idx 322, :chip/size :md}
                  {:x 2, :y 3, :z 3, :chip/color :red, :chip/idx 323, :chip/size :sm}
                  {:x 3, :y 3, :z 1, :chip/color :green, :chip/idx 331, :chip/size :lg}
                  {:x 3, :y 3, :z 2, :chip/color :green, :chip/idx 332, :chip/size :md}
                  {:x 3, :y 3, :z 3, :chip/color :red, :chip/idx 333, :chip/size :sm}))

(deftest did-someone-win?-test
  (testing "all winning lines return :blue"
    (is (= (distinct (map #(victory/did-someone-win?
                            (chips/replace-with (chips/create-chips) %))
                          victory/wins))
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
         (chips/replace-with (chips/create-chips) %))
       victory/wins)

  )
