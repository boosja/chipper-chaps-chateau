(ns chipper-chaps-chateau.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes HEAD]]
            [compojure.route :as route]
            [org.httpkit.server :as server]))

(def storage (System/getenv "GARDEN_STORAGE"))

(defn log-404 [edn]
  (let [file (str storage "/bads/unwanted-attention.log")]
    (io/make-parents file)
    (spit file edn :append true)))

(defn not-found-inspector [req]
  (let [[uri method] ((juxt :uri :request-method) req)
        edn (str {:date (str (java.time.Instant/now))
                  :method method
                  :uri uri}
                 "\n")]
    (log-404 edn)
    {:body "<html><body><h1>404 Not Found</h1></body></html>"}))

(defroutes app-routes
  (HEAD "/" _ {:status 202})
  (route/files "/")
  (route/files "/js" {:root (str storage "/public/js")})
  (route/not-found not-found-inspector))

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
