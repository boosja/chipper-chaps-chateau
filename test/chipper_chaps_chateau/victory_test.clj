(ns chipper-chaps-chateau.victory-test
  (:require [chipper-chaps-chateau.victory :as victory]
            [clojure.test :refer [deftest is testing]]))

(deftest vals->sets-test
  (testing "The vals of the map are converted to sets"
    (is (= (victory/vals->sets {:blue [{:x 1 :y 1 :z 1}
                                       {:x 2 :y 2 :z 2}
                                       {:x 3 :y 3 :z 3}]})
           {:blue #{{:x 1 :y 1 :z 1}
                    {:x 2 :y 2 :z 2}
                    {:x 3 :y 3 :z 3}}}))))
