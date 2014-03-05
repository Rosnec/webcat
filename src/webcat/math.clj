(ns webcat.math
  "Various mathematical stuff."
  (:require [clojure.math.numeric-tower :as num]
            [incanter.stats :as stats]
            [webcat.util :as util]))

(defn root
  "Compute the `n`th root of `x`"
  ([x n] (num/expt x (/ n))))

(defn factorial
  "Compute `n` factorial."
  ([n] (if (integer? n) (cond (zero? n) 1
                              (pos? n) (apply * (range 1 (inc n)))))))

(defn sd-from-mean
  "Calculates how many `sd`'s `x` is from `mean`"
  ([x mean std] (/ (- x mean)
                  std)))

(defn sd-from-mean-squared
  "Calculates the square of how many `sd`'s `x` is from `mean`"
  ([x mean std] (num/expt (sd-from-mean x mean std) 2)))

(defn sd-of-map
  "Returns a map with the standard deviations from the mean of `m`'s values."
  ([m] (let [mean (stats/mean (vals m))
             std  (stats/sd   (vals m))]
         (util/map-values (fn [x] (sd-from-mean x mean std))
                          m))))

(defn sd-squared-of-map
  "Returns a map with the standard deviations from the mean of `m`'s values."
  ([m] (let [mean (stats/mean (vals m))
             std  (stats/sd   (vals m))]
         (util/map-values (fn [x] (sd-from-mean-squared x mean std))
                          m))))

(defmacro score
  "Scores a mapping of numbers"
  ([m] (sd-squared-of-map m)))

(defn compare-words
  ([& maps] (let [shared-words (apply util/matching-values maps)]
              (if (empty? shared-words)
                nil
                (apply stats/euclidean-distance
                       (apply map list shared-words))))))

(defn best-match
  "Finds the map in `coll` which has the smallest euclidean distance from
  the map `m`."
  ([m coll]
     (reduce (fn [[old-key old-val] [new-key new-val]]
               (let [new-score (compare-words m new-val)
                     old-score (if (map? old-val)
                                 (compare-words m old-val)
                                 old-val)]
                 (if (< new-score old-score)
                   [new-key new-score]
                   [old-key old-score])))
             coll)))

(defn mean-distance
  "Finds the mean distance from the map `m` to the map-of-maps `maps`."
  ([m maps]
     (let [scores (filter number? (for [[url words] maps]
                                    (compare-words m words)))]
       (stats/mean scores))))
