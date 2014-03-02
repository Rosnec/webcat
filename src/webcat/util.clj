(ns webcat.util
  "Miscellaneous functions.")

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
