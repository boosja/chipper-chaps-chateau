(ns hello-world)

(str "hello, " "world!")

(->>
 (repeat 8 "🐣")
 (repeat 6)
 (interpose ["🐺"])
 (into [] cat))

(comment
  (require '[nextjournal.clerk :as clerk])

  ;; Clerk build from REPL
  (clerk/build! {:paths ["notebooks/*"] :out-path "public/clerk"})

  ;; Clerk build from terminal
  ;; clj -X:dev nextjournal.clerk/build! '{:paths ["notebooks/*"] :out-path "public/clerk"}'

  ;; On my machine, REPL build finishes in 21 ms, terminal build finishes in 2.5
  ;; seconds, including Clojure process overhead.
  )
