(ns chipper-chaps-chateau.board
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.player :as player]))

(defn select-chip [db & [[chip next-player]]]
  (let [game (db/current-game db)]
    [[:effect/transact [{:chip/id (:chip/id chip)
                         :chip/color (:game/current-color game)}
                        {:game/id (:game/id game)
                         :game/current-color (or next-player
                                                 (player/next (:game/current-color game)))}]]]))

(defn reset [_db dimension]
  [[:effect/transact [{:db/id "new-game"
                       :game/id [:data-require :id/gen]
                       :game/current-color :blue
                       :game/chips [:data-require :chips/gen dimension]}
                      {:db/ident :app/state
                       :app/current-game "new-game"}]]])

(defn perform-action [db [action & args]]
  (case action
    :board/select-chip (select-chip db args)
    :board/reset (reset db (first args))
    nil))
