(ns chipper-chaps-chateau.victory
  (:require [clojure.set :as set]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.wins :as wins]))

(defn has-three-in-a-row? [chips]
  (some #(set/subset? % chips) wins/d3))

(defn did-someone-win? [chips]
  (let [filtered (filter :chip/color chips)
        grouped (-> (group-by :chip/color filtered)
                    (update-vals #(set (map :point %))))
        winner (reduce (fn [winner? [color chips]]
                         (if (has-three-in-a-row? chips)
                           color winner?))
                       false grouped)]
    (cond
      winner winner
      (= (count chips) (count filtered)) :tie
      :else false)))

(defn heat-mapped-chips []
  (->> (chips/create-chips)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d3)) c]))
       (map #(assoc (second %) :chip/color (get {13 :blue-2
                                                 7 :blue
                                                 5 :blue-1
                                                 4 :blue-0}
                                                (first %))))))
