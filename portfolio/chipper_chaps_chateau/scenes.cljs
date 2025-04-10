(ns chipper-chaps-chateau.scenes
  (:require [chipper-chaps-chateau.bar-scenes]
            [portfolio.ui :as ui]))

:chipper-chaps-chateau.bar-scenes/keep

(ui/start!
 {:config {:css-paths ["/css/style.css"]}})

(defn init [])
