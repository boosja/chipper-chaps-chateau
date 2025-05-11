(ns chipper-chaps-chateau.scenes
  (:require [chipper-chaps-chateau.bar-scenes]
            [chipper-chaps-chateau.bartial-scenes]
            [chipper-chaps-chateau.el-chateau-scenes]
            [portfolio.ui :as ui]
            [replicant.dom :as d]))

:chipper-chaps-chateau.bar-scenes/keep
:chipper-chaps-chateau.bartial-scenes/keep
:chipper-chaps-chateau.el-chateau-scenes/keep

(ui/start!
 {:config {:css-paths ["/css/style.css"
                       "/css/portfolio.css"]}})

(defn init [])

(d/set-dispatch!
 (fn [_ data]
   (prn data)))
