(ns chipper-chaps-chateau.db
  (:require [datascript.core :as ds]))

(defn get-global [db k]
  (get (ds/entity db k) k :404))

(defn ->global-tx [k v]
  {:db/ident k k v})

(defn app-state [db]
  (ds/entity db :app/state))

(defn location [db]
  (:app/location (app-state db)))

(defn current-game [db]
  (:app/current-game (app-state db)))

(defn settings [db]
  (ds/entity db :settings))

(defn get-chips [db]
  (->> (ds/q '[:find [?e ...]
               :where
               [?e :x ?x]]
             db)
       (map #(->> % (ds/entity db) (into {})))
       (sort-by :point)))
