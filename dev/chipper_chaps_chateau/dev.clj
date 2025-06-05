(ns chipper-chaps-chateau.dev
  (:require [chipper-chaps-chateau.wins :as wins]
            [nextjournal.clerk :as clerk]))

(comment

  (clerk/serve! {})
  (clerk/show! "notebooks/scoring.clj")
  (clerk/halt!)

  (clerk/clear-cache!)

  (clerk/build! {:paths ["notebooks/*"]
                 :out-path "public/clerk"})
  )

(comment

  (def d2 (wins/find-collinear-triplets (for [y (range 1 4)
                                              x (range 1 4)]
                                          [x y])))

  (def d3 (wins/find-collinear-triplets (for [y (range 1 4)
                                              x (range 1 4)
                                              z (range 1 4)]
                                          [x y z])))

  (def d4 (wins/find-collinear-triplets (for [w (range 1 4)
                                              y (range 1 4)
                                              x (range 1 4)
                                              z (range 1 4)]
                                          [x y z w])))

  (def d5 (wins/find-collinear-triplets (for [v (range 1 4)
                                              w (range 1 4)
                                              y (range 1 4)
                                              x (range 1 4)
                                              z (range 1 4)]
                                          [x y z w v])))

  (defn wins->edn [path wins]
    (->> (str "[" (clojure.string/join "\n " wins) "]")
         (spit path)))

  (wins->edn "public/wins-3d.edn" wins/d3)
  (wins->edn "public/wins-4d.edn" wins/d4)
  (wins->edn "public/wins-5d.edn" wins/d5)

  )
