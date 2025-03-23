(ns chipper-chaps-chateau.server
  (:require [compojure.core :refer [defroutes HEAD]]
            [compojure.route :as route]
            [org.httpkit.server :as server]))

(defroutes app-routes
  (HEAD "/" req {:status 202})
  (route/files "/")
  (route/files "/js" {:root (str (System/getenv "GARDEN_STORAGE") "/public/js")})
  )

(def wrapped-app
  app-routes)

(defn start! [opts]
  (let [server (server/run-server #'wrapped-app
                                  (merge {:legacy-return-value? false
                                          :host "0.0.0.0"
                                          :port 7777}
                                         opts))]
    (println (format "Chipper Chap says hello on port %s"
                     (server/server-port server)))))
