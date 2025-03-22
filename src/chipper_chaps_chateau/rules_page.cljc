(ns chipper-chaps-chateau.rules-page
  (:require [chipper-chaps-chateau.la-visual :as vis]
            [chipper-chaps-chateau.db :as db]
            [chipper-chaps-chateau.victory :as victory]
            [chipper-chaps-chateau.chips :as chips]))

(defn render [db]
  (let [show-all? (db/get-global db :rules/show-all?)
        chips (chips/create-chips)
        filtered [0 4 8 10 22 28 42 47 35]]
    (list
     (vis/box {:color :green
               :text (if show-all? "All scenarios" "How to win")
               :actions [[:action/transact [(db/->global-tx :rules/show-all?
                                                            (not show-all?))]]]})
     [:div.rules
      (if show-all?
        (for [winning-line victory/wins]
          [:div.wrapper
           [::vis/board.board.board-sm
            {::vis/data (chips/->cells (chips/replace-with chips winning-line)
                                    (fn [_]))}
            [::vis/cell.cell
             [::vis/chip.chip]]]])

        (for [w-idx filtered]
          [:div.wrapper
           [::vis/board.board.board-sm
            {::vis/data (chips/->cells (chips/replace-with chips (nth victory/wins w-idx))
                                    (fn [_]))}
            [::vis/cell.cell
             [::vis/chip.chip]]]]))])))
