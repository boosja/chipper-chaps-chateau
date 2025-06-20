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

;; Too slow, gets slower and slower further out in the game
(defn did-someone-win?-2 [chips]
  (let [grouped (->> chips
                     (filter :chip/color)
                     (group-by :chip/color))
        wins (update-vals grouped (comp wins/find-collinear-triplets
                                        #(mapv :point %)))]
    (ffirst (filter #(< 0 (count (second %))) wins))))

(defn heat-mapped-chips []
  (->> (chips/create-chips-3d)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d3)) c]))
       (map #(assoc (second %) :chip/color (get {13 :blue-2
                                                 7 :blue
                                                 5 :blue-1
                                                 4 :blue-0}
                                                (first %))))))

(defn heat-mapped-chips-4d []
  (->> (chips/create-chips-4d)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d4)) c]))
       (map #(assoc (second %) :chip/color (get {40 :blue-2
                                                 15 :blue
                                                 14 :blue-1
                                                 8 :blue-0
                                                 7 :white}
                                                (first %))))))

(defn heat-mapped-chips-5d []
  (->> (chips/create-chips-5d)
       (map (fn [c]
              [(count (filter #(contains? % (:point c)) wins/d5)) c]))
       (map #(assoc (second %) :chip/color (get {121 :blue-2
                                                 41 :blue
                                                 31 :blue-1
                                                 16 :blue-0
                                                 11 :white}
                                                (first %))))))
