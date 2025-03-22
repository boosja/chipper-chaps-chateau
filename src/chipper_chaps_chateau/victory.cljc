(ns chipper-chaps-chateau.victory
  (:require [clojure.set :as set]))

(def wins [ ;; horizontal (rows)
           #{{:y 1, :x 1, :z 1} {:y 1, :x 2, :z 1} {:y 1, :x 3, :z 1}}
           #{{:y 2, :x 1, :z 1} {:y 2, :x 2, :z 1} {:y 2, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 1} {:y 3, :x 2, :z 1} {:y 3, :x 3, :z 1}}
           #{{:y 1, :x 1, :z 2} {:y 1, :x 2, :z 2} {:y 1, :x 3, :z 2}}
           #{{:y 2, :x 1, :z 2} {:y 2, :x 2, :z 2} {:y 2, :x 3, :z 2}}
           #{{:y 3, :x 1, :z 2} {:y 3, :x 2, :z 2} {:y 3, :x 3, :z 2}}
           #{{:y 1, :x 1, :z 3} {:y 1, :x 2, :z 3} {:y 1, :x 3, :z 3}}
           #{{:y 2, :x 1, :z 3} {:y 2, :x 2, :z 3} {:y 2, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 3} {:y 3, :x 2, :z 3} {:y 3, :x 3, :z 3}}
           ;; vertical (cols)
           #{{:y 1, :x 1, :z 1} {:y 2, :x 1, :z 1} {:y 3, :x 1, :z 1}}
           #{{:y 1, :x 2, :z 1} {:y 2, :x 2, :z 1} {:y 3, :x 2, :z 1}}
           #{{:y 1, :x 3, :z 1} {:y 2, :x 3, :z 1} {:y 3, :x 3, :z 1}}
           #{{:y 1, :x 1, :z 2} {:y 2, :x 1, :z 2} {:y 3, :x 1, :z 2}}
           #{{:y 1, :x 2, :z 2} {:y 2, :x 2, :z 2} {:y 3, :x 2, :z 2}}
           #{{:y 1, :x 3, :z 2} {:y 2, :x 3, :z 2} {:y 3, :x 3, :z 2}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 1, :z 3} {:y 3, :x 1, :z 3}}
           #{{:y 1, :x 2, :z 3} {:y 2, :x 2, :z 3} {:y 3, :x 2, :z 3}}
           #{{:y 1, :x 3, :z 3} {:y 2, :x 3, :z 3} {:y 3, :x 3, :z 3}}
           ;; depth (z)
           #{{:y 1, :x 1, :z 1} {:y 1, :x 1, :z 2} {:y 1, :x 1, :z 3}}
           #{{:y 1, :x 2, :z 1} {:y 1, :x 2, :z 2} {:y 1, :x 2, :z 3}}
           #{{:y 1, :x 3, :z 1} {:y 1, :x 3, :z 2} {:y 1, :x 3, :z 3}}
           #{{:y 2, :x 1, :z 1} {:y 2, :x 1, :z 2} {:y 2, :x 1, :z 3}}
           #{{:y 2, :x 2, :z 1} {:y 2, :x 2, :z 2} {:y 2, :x 2, :z 3}}
           #{{:y 2, :x 3, :z 1} {:y 2, :x 3, :z 2} {:y 2, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 1} {:y 3, :x 1, :z 2} {:y 3, :x 1, :z 3}}
           #{{:y 3, :x 2, :z 1} {:y 3, :x 2, :z 2} {:y 3, :x 2, :z 3}}
           #{{:y 3, :x 3, :z 1} {:y 3, :x 3, :z 2} {:y 3, :x 3, :z 3}}
           ;; diagonal
           #{{:y 1, :x 1, :z 1} {:y 2, :x 2, :z 1} {:y 3, :x 3, :z 1}}
           #{{:y 1, :x 1, :z 2} {:y 2, :x 2, :z 2} {:y 3, :x 3, :z 2}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 2, :z 3} {:y 3, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 1} {:y 2, :x 2, :z 1} {:y 1, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 2} {:y 2, :x 2, :z 2} {:y 1, :x 3, :z 2}}
           #{{:y 3, :x 1, :z 3} {:y 2, :x 2, :z 3} {:y 1, :x 3, :z 3}}
           ;; depth horizontal diagonal (rows)
           #{{:y 1, :x 1, :z 1} {:y 1, :x 2, :z 2} {:y 1, :x 3, :z 3}}
           #{{:y 2, :x 1, :z 1} {:y 2, :x 2, :z 2} {:y 2, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 1} {:y 3, :x 2, :z 2} {:y 3, :x 3, :z 3}}
           #{{:y 1, :x 1, :z 3} {:y 1, :x 2, :z 2} {:y 1, :x 3, :z 1}}
           #{{:y 2, :x 1, :z 3} {:y 2, :x 2, :z 2} {:y 2, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 3} {:y 3, :x 2, :z 2} {:y 3, :x 3, :z 1}}
           ;; depth verical diagonal (cols)
           #{{:y 1, :x 1, :z 1} {:y 2, :x 1, :z 2} {:y 3, :x 1, :z 3}}
           #{{:y 1, :x 2, :z 1} {:y 2, :x 2, :z 2} {:y 3, :x 2, :z 3}}
           #{{:y 1, :x 3, :z 1} {:y 2, :x 3, :z 2} {:y 3, :x 3, :z 3}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 1, :z 2} {:y 3, :x 1, :z 1}}
           #{{:y 1, :x 2, :z 3} {:y 2, :x 2, :z 2} {:y 3, :x 2, :z 1}}
           #{{:y 1, :x 3, :z 3} {:y 2, :x 3, :z 2} {:y 3, :x 3, :z 1}}
           ;; depth diagonal diagonal
           #{{:y 1, :x 1, :z 1} {:y 2, :x 2, :z 2} {:y 3, :x 3, :z 3}}
           #{{:y 1, :x 1, :z 3} {:y 2, :x 2, :z 2} {:y 3, :x 3, :z 1}}
           #{{:y 3, :x 1, :z 1} {:y 2, :x 2, :z 2} {:y 1, :x 3, :z 3}}
           #{{:y 3, :x 1, :z 3} {:y 2, :x 2, :z 2} {:y 1, :x 3, :z 1}}
           ])

(defn vals->sets [m]
  (update-vals m (fn [ps]
                   (set (map #(select-keys % [:x :y :z]) ps)))))

(defn has-three-in-a-row? [chips]
  (some #(set/subset? % chips) wins))

(defn did-someone-win? [chips]
  (let [filtered (filter :chip/color chips)
        grouped (->> filtered
                     (group-by :chip/color)
                     (vals->sets))
        winner (reduce (fn [winner [color chips]]
                               (if (has-three-in-a-row? chips)
                                 color
                                 winner))
                             false
                             grouped)]
    (cond
      winner
      winner

      (= (count chips) (count filtered))
      :tie

      :else
      false)))
