(ns chipper-chaps-chateau.dev
  (:require [chipper-chaps-chateau.app :as app]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.id :as id]
            [chipper-chaps-chateau.victory :as victory]
            [datascript.core :as ds]
            [dataspex.core :as dataspex]))

(defn override-board! [chips]
  (ds/transact app/conn [{:db/id "new-game"
                          :game/id (id/gen!)
                          :game/current-color :blue
                          :game/chips (id/-ilize! :chip/id chips)}
                         {:db/ident :app/state
                          :app/current-game "new-game"}]))

(comment
  (def db (ds/db app/conn))

  (def current-game (db/current-game db))
  (->> (:game/chips current-game)
       (map #(into {} %))
       (sort-by (comp vec reverse :point))
       (partition-all 27))

  (def cs (db/get-chips db))
  cs

  (override-board! (chips/colored-chateaus))
  (override-board! (chips/colored-chateaus-switch :blue))
  (override-board! (chips/colored-chateaus-switch :red))
  (override-board! (chips/colored-chateaus-switch :green))
  (override-board! (chips/colored-chateaus-switch :yellow))
  (override-board! (chips/colored-chateaus-switch-cycle))
  (override-board! (victory/heat-mapped-chips))
  (override-board! (victory/heat-mapped-chips-4d))
  (override-board! (victory/heat-mapped-chips-5d))

  ;; Reset game
  (override-board! (chips/create-chips :dim/three))

  ;; Navigate
  (do (app/dispatch nil [[:action/navigate :route/d1]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/one))))
  (do (app/dispatch nil [[:action/navigate :route/d2]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/two))))
  (do (app/dispatch nil [[:action/navigate :route/d3]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/three))))
  (do (app/dispatch nil [[:action/navigate :route/d4]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/four))))
  (do (app/dispatch nil [[:action/navigate :route/d5]])
      (override-board! (id/-ilize! :chip/id (chips/create-chips :dim/five))))

  )

(comment

  (dataspex/inspect "Chipper DB" app/conn)

  )
