(ns chipper-chaps-chateau.la-visual-test
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [clojure.test :refer [deftest is testing]]
            [replicant.alias :refer [expand-1]]))

#_(deftest chip-test
  (testing "merges in size and color classes"
    (is (= (-> (expand-1 [::vis/chip.chip {::vis/data {:size :lg
                                                       :actions [[]]}}])
               second
               :class)
           #{"chip" "white" "large"})))

  (testing "click event is added"
    (is (= (-> (expand-1 [::vis/chip.chip {::vis/data {:size :lg
                                                       :actions [[]]}}])
               second
               :on)
           {:click [[]]}))))
