(ns chipper-chaps-chateau.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes HEAD wrap-routes]]
            [compojure.route :as route]
            [org.httpkit.server :as server]))

(def storage (System/getenv "GARDEN_STORAGE"))

(defn ->smap [m]
  (-> {:date (str (java.time.Instant/now))}
      (merge m)
      (str "\n")))

(defn log->file [path m]
  (let [file (str storage path)]
    (io/make-parents file)
    (spit file (->smap m) :append true)))

(defn log-stats [handler]
  (fn [request]
    (when (= (:uri request) "/")
      (log->file "/stats.log" {:uri (:uri request)}))
    (handler request)))

(defn not-found-inspector [req]
  (let [[uri method] ((juxt :uri :request-method) req)]
    (log->file "/bads/unwanted-attention.log"
               {:method method :uri uri})
    {:body "<html><body><h1>404 Not Found</h1></body></html>"}))

(defroutes app-routes
  (HEAD "/" _ {:status 202})
  (route/files "/")
  (route/files "/js" {:root (str storage "/public/js")})
  (route/files "/clerk" {:root (str storage "/public/clerk")})
  (route/not-found not-found-inspector))

(def wrapped-app
  (-> app-routes
      (wrap-routes log-stats)))

(defn start! [opts]
  (let [server (server/run-server #'wrapped-app
                                  (merge {:legacy-return-value? false
                                          :host "0.0.0.0"
                                          :port 7777}
                                         opts))]
    (println (format "Chipper Chap says hello on port %s"
                     (server/server-port server)))))
