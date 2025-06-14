(ns chipper-chaps-chateau.player
  (:refer-clojure :exclude [next]))

(def colors [:blue :red :green :yellow])

(def next (-> (into {} (partitionv 2 1 colors))
              (assoc (last colors) (first colors))))
