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
   [::vis/board.board
    {::vis/chips (chips/create-chips :dim/three)}
    [:circle.shadow]
    [:circle.pointer.darken]]])

(defscene colored-in
  "normal | colorblind"
  [:div.pfo-flex
   [::box.pfo-h.pfo-w
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box.pfo-h.pfo-w.colorblind
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus)}
     [:circle.shadow]
     [:circle.pointer.darken]]]])

(defscene pattern
  "normal | colorblind"
  [:div.pfo-grid-2.pfo-h-3
   [::box
    [::vis/board.board
     {::vis/chips colored1/board}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box.colorblind
    [::box
     [::vis/board.board
      {::vis/chips colored1/board}
      [:circle.shadow]
      [:circle.pointer.darken]]]]

   [::box
    [::box
    [::vis/board.board
     {::vis/chips colored1/board2}
     [:circle.shadow]
     [:circle.pointer.darken]]]]

   [::box.colorblind
    [::box
    [::vis/board.board
     {::vis/chips colored1/board2}
     [:circle.shadow]
     [:circle.pointer.darken]]]]])

(defscene colored-in-switch
  "normal | colorblind"
  [:div.pfo-grid.pfo-h-2
   [::box
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :blue)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box.colorblind
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :blue)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :red)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box.colorblind
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :red)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :green)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box.colorblind
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :green)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :yellow)}
     [:circle.shadow]
     [:circle.pointer.darken]]]

   [::box.colorblind
    [::vis/board.board
     {::vis/chips (chips/colored-chateaus-switch :yellow)}
     [:circle.shadow]
     [:circle.pointer.darken]]]])
