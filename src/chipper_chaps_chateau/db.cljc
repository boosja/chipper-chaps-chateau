(ns chipper-chaps-chateau.db
  (:require [datascript.core :as ds]))

(defn get-global [db k]
  (get (ds/entity db k) k :404))

(defn ->global-tx [k v]
  {:db/ident k k v})

(defn get-chips [db]
  (->> (ds/q '[:find [?e ...]
               :where
               [?e :x ?x]]
             db)
       (map #(->> % (ds/entity db) (into {})))
       (sort-by :chip/idx)))
