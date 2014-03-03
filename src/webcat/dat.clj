(ns webcat.dat
  "Maintains database of category -> URL -> web page content mappings"
  (:require [clojure.set :refer [intersection]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [webcat.util :as util]
            [webcat.web :as web]
            [webcat.words :as words])
  (:import [java.io PushbackReader]))

(defn make-page
  "Creates a new page entry using the words parsed from the given `url`,
  filtered with the predicate function `pred`."
  ([url] (make-page url identity (constantly true)))
  ([url func] (make-page url func (constantly true)))
  ([url func pred] (into (sorted-map)
                         (words/word-scores (web/url-text url)
                                            1000
                                            func
                                            pred))))

(defn combine-word-maps
  "Combines a sequence of word-maps"
  ([maps] (let [total (apply + (flatten (map vals maps)))]
            (apply merge-with util/mean maps))))

(defn average-category
  "Averages `category`'s sites' word maps into a single word map."
  ([category] (combine-word-maps (vals category))))

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
  url in its category, `category` is removed from the database. If no
  `category` is given, removes `url` from all categories.
  Returns the modified database."
  ([url] (let [categories (for [[k v] @database :when (v url)] k)]
           (dorun (map (fn [category] (remove-site url category))
                       categories))
           @database))
  ([url category] (dosync (alter database util/dissoc-in
                                 [category url]))))

(defn compare-words
  "Compares an ordered sequence of words to a category"
  ([words category] (compare-words words category 10))
  ([words category n] (let [common-words (intersection (set words)
                                                       (set (keys category)))]
                        (apply + (for [[n word] (util/indexed words)]
                                   (let [score (category word)]
                                     (* score (util/root 2 (inc n)))))))))

(defn compare-url
  "Compares the words from the webpage at `url` to the given category, using
  the top `n` word matches, or top 10 words if `n` is not provided."
  ([url word-map] (compare-url url word-map 10))
  ([url word-map n] (let [url-map (words/word-scores (web/url-text url)
                                                     1000
                                                     string/lower-case
                                                     word-filter)
                          url-words (util/ordered-keys url-map)]
                      (compare-words url-words word-map n))))

(defn url->category
  "Finds the category in the database which best fits `url`"
  ([url] (url->category 10))
  ([url n] (util/map-max-key @database
                             (fn [category]
                               (compare-url url
                                            (average-category category)
                                            n)))))

(defn url->url
  "Finds the url in `category` which best fits `url`."
  ([url category] (url->url url category 10))
  ([url category n] (util/map-max-key category
                                      (fn [page] (compare-url url page n)))))

(defn save-backup
  "Save the database to a file."
  ([file] (spit file (str @database))))

(defn load-backup
  "Load the database from file, clearing the existing one."
  ([file] (let [backup (with-open [rdr (io/reader file)]
                             (read (PushbackReader. rdr)))]
            (dosync (ref-set database backup)))))

