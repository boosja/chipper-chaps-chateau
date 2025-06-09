(ns chipper-chaps-chateau.victory
  (:require [clojure.set :as set]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.wins :as wins]))

(defn three-in-a-row? [chips wins]
  (some #(set/subset? % chips) wins))

(defn has-winner? [chips wins]
  (let [filtered (filter :chip/color chips)
        grouped (-> (group-by :chip/color filtered)
                    (update-vals #(set (map :point %))))
        winner (reduce (fn [winner? [color chips]]
                         (if (three-in-a-row? chips wins)
                           color
                           winner?))
                       false
                       grouped)]
    (cond
      winner winner
      (= (count chips) (count filtered)) :tie
      :else false)))

(defn heat-mapped-chips []
  (->> (chips/create-chips :dim/three)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d3)) c]))
       (map #(assoc (second %) :chip/color (get {13 :blue-2
                                                 7 :blue
                                                 5 :blue-1
                                                 4 :blue-0}
                                                (first %))))))

(defn heat-mapped-chips-4d []
  (->> (chips/create-chips :dim/four)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d4)) c]))
       (map #(assoc (second %) :chip/color (get {40 :blue-2
                                                 15 :blue
                                                 14 :blue-1
                                                 8 :blue-0
                                                 7 :white}
                                                (first %))))))

(defn heat-mapped-chips-5d []
  (->> (chips/create-chips :dim/five)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d5)) c]))
       (map #(assoc (second %) :chip/color (get {121 :blue-2
                                                 41 :blue
                                                 31 :blue-1
                                                 16 :blue-0
                                                 11 :white}
                                                (first %))))))
