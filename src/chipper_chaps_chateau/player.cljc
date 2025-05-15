(ns chipper-chaps-chateau.player
  (:refer-clojure :exclude [next]))

(def next
  {:blue :red
   :red :green
   :green :yellow
   :yellow :blue})
