(ns chipper-chaps-chateau.board
  (:require [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.player :as player]))

(defn select-chip [db & [[chip-id next-player]]]
  (let [game (db/current-game db)]
    [[:effect/transact [{:chip/id (:chip/id chip-id)
                         :chip/color (:game/current-color game)}
                        {:game/id (:game/id game)
                         :game/current-color (or next-player
                                                 (player/next (:game/current-color game)))}]]]))

(defn reset-d1 [_db]
  [[:effect/transact [{:db/id "new-game"
                       :game/id [:data-require :id/gen]
                       :game/current-color :blue
                       :game/chips [:data-require :id.gen/d1-chips]}
                      {:db/ident :app/state
                       :app/current-game "new-game"}]]])

(defn reset-d3 [_db]
  [[:effect/transact [{:db/id "new-game"
                       :game/id [:data-require :id/gen]
                       :game/current-color :blue
                       :game/chips [:data-require :id.gen/d3-chips]}
                      {:db/ident :app/state
                       :app/current-game "new-game"}]]])

(defn reset-d4 [_db]
  [[:effect/transact [{:db/id "new-game"
                       :game/id [:data-require :id/gen]
                       :game/current-color :blue
                       :game/chips [:data-require :id.gen/d4-chips]}
                      {:db/ident :app/state
                       :app/current-game "new-game"}]]])

(defn reset-d5 [_db]
  [[:effect/transact [{:db/id "new-game"
                       :game/id [:data-require :id/gen]
                       :game/current-color :blue
                       :game/chips [:data-require :id.gen/d5-chips]}
                      {:db/ident :app/state
                       :app/current-game "new-game"}]]])

(defn perform-action [db [action & args]]
  (case action
    :board/select-chip (select-chip db args)
    :board.d1/reset (reset-d1 db)
    :board.d3/reset (reset-d3 db)
    :board.d4/reset (reset-d4 db)
    :board.d5/reset (reset-d5 db)
    nil))
