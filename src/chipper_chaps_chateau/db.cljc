(ns chipper-chaps-chateau.db
  (:require [datascript.core :as ds]))

(defn get-global [db k]
  (get (ds/entity db k) k :404))

(defn ->global-tx [k v]
  {:db/ident k k v})

(defn create-chips []
  (for [i (range 3)
        j (range 3)
        k (range 3)]
    (let [x (inc j)
          y (inc i)
          z (inc k)]
      {:x x
       :y y
       :z z
       :chip/size (get {1 :lg 2 :md 3 :sm} z)
       :chip/idx (+ (* 100 y) (* 10 x) (* 1 z))})))

(defn get-chips [db]
  (->> (ds/q '[:find [?e ...]
               :where
               [?e :x ?x]]
             db)
       (map #(->> % (ds/entity db) (into {})))
       (sort-by :chip/idx)))

(defn ->cells [chips get-actions]
  (->> chips
       (partition 3)
       (mapv
        #(mapv (fn [{:keys [chip/size chip/color] :as chip}]
                 {:size size
                  :color color
                  :actions (get-actions chip)})
               %))))
