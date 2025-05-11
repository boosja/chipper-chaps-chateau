(ns chipper-chaps-chateau.el-chateau-scenes
  (:require [chipper-chaps-chateau.boards.colored1 :as colored1]
            [chipper-chaps-chateau.chips :as chips]
            [chipper-chaps-chateau.la-visual :as vis]
            [portfolio.replicant :refer-macros [defscene]]
            [replicant.alias :refer [defalias]]))

(defalias box [attrs content]
  [:div.portfolio-chateau {:class (:class attrs)}
   content])

(defscene el-chateau
  [::box.pfo-h.pfo-w
   (vis/el-chateau nil (chips/create-chips))])

(defscene colored-in
  "normal | colorblind"
  [:div.pfo-flex
   [::box.pfo-h.pfo-w
    (vis/el-chateau nil (chips/colored-chateaus))]
   [::box.pfo-h.pfo-w.colorblind
    (vis/el-chateau nil (chips/colored-chateaus))]])

(defscene pattern
  "normal | colorblind"
  [:div.pfo-grid-2.pfo-h-3
   [::box (vis/el-chateau nil colored1/board)]
   [::box.colorblind (vis/el-chateau nil colored1/board)]
   [::box (vis/el-chateau nil colored1/board2)]
   [::box.colorblind (vis/el-chateau nil colored1/board2)]])

(defscene colored-in-switch
  "normal | colorblind"
  [:div.pfo-grid.pfo-h-2
   [::box (vis/el-chateau nil (chips/colored-chateaus-switch :blue))]
   [::box.colorblind (vis/el-chateau nil (chips/colored-chateaus-switch :blue))]
   [::box (vis/el-chateau nil (chips/colored-chateaus-switch :red))]
   [::box.colorblind (vis/el-chateau nil (chips/colored-chateaus-switch :red))]
   [::box (vis/el-chateau nil (chips/colored-chateaus-switch :green))]
   [::box.colorblind (vis/el-chateau nil (chips/colored-chateaus-switch :green))]
   [::box (vis/el-chateau nil (chips/colored-chateaus-switch :yellow))]
   [::box.colorblind (vis/el-chateau nil (chips/colored-chateaus-switch :yellow))]])
