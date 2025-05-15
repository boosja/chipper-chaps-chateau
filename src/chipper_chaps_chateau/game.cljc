(ns chipper-chaps-chateau.game
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.player :as player]))

(defn pick [db chip-id]
  (let [game (db/current-game db)]
    [[:effect/transact [{:chip/id (:chip/id chip-id)
                         :chip/color (:game/current-color game)}
                        {:game/id (:game/id game)
                         :game/current-color (player/next (:game/current-color game))}]]]))

(defn perform-action [db [action & args]]
  (case action
    :game/pick (pick db (first args))
    nil))
