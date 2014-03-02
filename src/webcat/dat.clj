(ns webcat.dat
  "Maintains database of URL -> category mappings"
  (:require [clojure.string :as string]
            [webcat.util :as util]
            [webcat.web :as web]
            [webcat.words :as words]))

(defrecord BiMap [keyval valkeys])

(defn bi-map
  "Create a BiMap out of a regular map. A BiMap is a two-way map. In one
  direction we map key -> value. In the other direction, we map value -> keys,
  where keys is the set of keys which map to that value."
  ([m] (BiMap. (into (sorted-map)
                     m)
               (into (sorted-map)
                     (util/map-invert* m)))))

(defn make-page
  "Creates a new page entry using the words parsed from the given `url`,
  filtered with the predicate function `pred`."
  ([url] (make-page url identity (constantly true)))
  ([url func] (make-page url func (constantly true)))
  ([url func pred] (bi-map (words/word-proportions (web/url-text url)
                                                   1000
                                                   func
                                                   pred))))

(defn combine-bi-maps
  "Creates a single bi-map from all of the provided bi-maps."
  ([bms] (let [combined (apply merge-with +
                               (for [b bms]
                                 (:keyval b)))]
           (bi-map combined))))

(defn word-filter
  "Filter to be used on the word lists from the websites. Returns
  logical true if the word should be included, and false if it should not.

  Discards words with length less than 5, or containing non-alphabetic
  characters."
  ([word] (not (or (< (count word)
                      5)
                   (re-find #"[^a-zA-Z]"
                            word)))))

(def database
  (ref {}))

(defn add-site
  "Adds `category` -> `url` -> page record for `url` to the database, creating
  any missing fields, and overwriting duplicate urls."
  ([category url] (dosync
                   (alter database assoc-in [category url]
                          (make-page url string/lower-case word-filter)))))

(defn clear-sites
  "Clears all sites from the database"
  ([] (dosync (ref-set database {}))))

(defn remove-site
  "Removes the given `url` from `category` in the database. If it is the only
  url in its category, the category is removed."
  ([category url] (dosync (alter database util/dissoc-in
                                 [category url]))))

(defn serialize
  "Serializes the database for saving to disc"
  ([] nil))

(defn deserialize
  "Deserializes a database saved to disc"
  ([serialized] nil))

(defn load-backup
  "Load a serialized database into the database"
  ([serialized]
     (let [backup (deserialize serialized)]
       (dosync (ref-set database backup)))))
