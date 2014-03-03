(ns webcat.util
  "Miscellaneous functions."
  (:require [clojure.math.numeric-tower :refer [expt]]))

(defn root
  "Compute the `n`th root of `x`"
  ([x n] (expt x (/ n))))

(defn mean
  "Compute the mean of the arguments"
  ([] nil)
  ([x] x)
  ([x y] (/ (+ x y) 2))
  ([x y & more] (/ (apply + x y more)
                   (+ 2 (count more)))))

(defn indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])

  Taken from contrib.seq"
  [s]
  (map vector (iterate inc 0) s))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn map-invert*
  "Inverts the key -> value mappings in m, transforming them into
  value -> #{key1 key2 ...} mappings."
  ([m] (apply merge-with
              into
              (for [[k v] m]
                {v #{k}}))))

(defn ordered-keys
  "Returns a sequence of the keys in `m` ordered by the values they map to."
  ([m] (keys (sort-by val m))))

(defn map-max-key
  "Finds the key which maps to the greatest value after mapping `func` to `m`.
  nil if the map is empty."
  ([m func] (if (empty? m)
              nil
              (key (apply max-key (comp func val) m)))))

(defn proportions
  "Takes a mapping of key -> val, where val is a number, and returns a new
  mapping, where val is now the proportion of val to the vals of the entire
  mapping, in parts per `n` (defaults to parts per 100, i.e. percent)."
  ([m] (proportions m 100))
  ([m n] (let [total (apply + (vals m))]
           (reduce (fn [r [k v]]
                     (assoc r k (int (* n
                                        (/ v total)))))
                   {}
                   m))))

(defn scores
  ""
  ([m]
     (scores m 100 -))
  ([m scale]
     (scores m scale -))
  ([m scale func]
     (let [number-keys (count m)
           total-value (apply + (vals m))]
       (reduce (fn [r [k v]]
                 (assoc r k
                        (int (* scale
                                (/ v
                                   (func total-value
                                         number-keys))))))
               {}
               m))))
