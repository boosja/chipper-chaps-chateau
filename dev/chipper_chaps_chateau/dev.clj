(ns chipper-chaps-chateau.dev
  (:require [nextjournal.clerk :as clerk]))

(comment

  (clerk/serve! {})
  (clerk/show! "notebooks/scoring.clj")

  (clerk/build! {:paths ["notebooks/*"]
                 :out-path "public/clerk"})

  )
