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

(defn score
  "Scores a mapping of numbers"
  ([m] (sd-of-map m)))

(defn compare-words
  ([m & maps] (let [shared-words  (apply util/matching-values 0 m maps)
                    number-shared (count (first shared-words))
                    number-in-m   (count m)]
              (if (empty? shared-words)
                nil
                (let [all-maps (cons m maps)
                      all-vals (map vals all-maps)
                      sums (apply map + m maps)
                      nums (map count all-maps)
                      total (apply + nums)
                      portions (map (fn [n] (/ n total)) nums)
                      expected-vals (map (fn [O P] (map (partial * P) O))
                                         all-vals portions)
                      scores (map (fn [obs exp]
                                    (apply + (map (fn [O E]
                                                    (/ (num/expt (- O E) 2)
                                                       E))
                                                  obs exp)))
                                  all-vals expected-vals)]
                  (println "S:" scores)
                  (first scores))))))

(defn best-match
  "Finds the map in `coll` which has the smallest euclidean distance from
  the map `m`."
  ([m coll]
     (reduce (fn [[old-key old-val] [new-key new-val]]
               (let [new-score (compare-words m new-val)
                     old-score (if (map? old-val)
                                 (compare-words m old-val)
                                 old-val)]
;                 (println "NEW!!!!" new-score "OLD!!!!" old-score)
                 (if (or (nil? old-score)
                         (and new-score
                              (< new-score old-score)))
                   [new-key new-score]
                   [old-key old-score])))
             coll)))

(defn mean-distance
  "Finds the mean distance from the map `m` to the map-of-maps `maps`."
  ([m maps]
     (let [scores (filter number? (for [[url words] maps]
                                    (compare-words m words)))]
       (stats/mean scores)))
  ([m maps n]
     (let [comp (if (pos? n)
                  util/top-pairs
                  util/bottom-pairs)
           scores (filter number?
                          (for [[url words] maps]
                            (compare-words m words)))
           best-scores (take (num/abs n)
                             ((if (pos? n)
                                identity
                                reverse)
                              scores))]
       (stats/mean best-scores))))
