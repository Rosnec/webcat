(ns webcat.dat
  "Maintains database of category -> URL -> web page content mappings"
  (:require [clojure.set :refer [intersection]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [webcat.math :as math]
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

;; (defn category-score
;;   "Finds the score of `category`. If `category` is a string, uses the
;;   appropriate category from the database. If `category` is a map, uses
;;   `category` by itself. Else returns nil."
;;   ([category] (math/score (cond (string? category) (@database category)
;;                                 (map? category) category))))

;; (defn compare-scores
;;   "Compares the scores of two categories"
;;   ([c1 c2] (math/score-similarity (vals c1) (vals c2))))

(defn compare-words
  "Compares an ordered sequence of words to a category"
  ([words category] (compare-words words category 10))
  ([words category n] (let [common-words (intersection (set words)
                                                       (set (keys category)))]
                        (apply + (for [[n word]
                                       (take n (util/indexed common-words))]
                                   (let [score (category word)]
                                     (println "score" score
                                              "word" word
                                              "n" n)
                                     (* score (util/root 2 (inc n)))))))))

(defn compare-url2
  "the re-comparening"
  [url word-map n] )

(defn compare-url
  "Compares the words from the webpage at `url` to the given word map, using
  the top `n` word matches, or top 10 words if `n` is not provided."
  ([url word-map] (let [url-map (make-page url)]
                      (math/compare-words url-map word-map))))

(defn url->category
  "Finds the category in the database which best fits `url`"
  ([url] (util/map-min-key @database
                           (fn [category]
                             (compare-url url (average-category category))))))

(defn url->url
  "Finds the url in `category` which best fits `url`."
  ([url category] (util/map-min-key category
                                    (fn [page] (compare-url url page)))))

(defn closest-url
  ""
  ([url] (let [site (make-page url)
               best-matches (for [[category sites] @database]
                              (math/best-match site sites))]
           (reduce (fn [[rk rv] [k v]] (if (> rv v)
                                        [rk rv]
                                         [k  v]))
                   best-matches))))

(defn save-backup
  "Save the database to a file."
  ([file] (spit file (str @database))))

(defn load-backup
  "Load the database from file, clearing the existing one."
  ([file] (let [backup (with-open [rdr (io/reader file)]
                             (read (PushbackReader. rdr)))]
            (dosync (ref-set database backup)))))



;; Database Generating Functions
;;         and some preset sites
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def wikipedia "https://en.wikipedia.org/wiki/")

(def countries ["China" "India" "United_States" "Indonesia" "Brazil"
                "Pakistan" "Nigeria" "Bangladesh" "Russia" "Japan"
                "Mexico" "Philippines" "Vietnam" "Ethiopia"])
(def rock-music ["Elvis_Presley" "Little_Richard" "Buddy_Holly" "The_Beatles"
                 "Creedence_Clearwater_Revival"
                 "The Doors" "Pink Floyd" "Led Zeppelin" "Black Sabbath"
                 "Derek_and_the_Dominos" "Fleetwood_Mac" "Allman_Brothers_Band"
                 "Queen"])
(def rap-music ["Run-D.M.C." "LL_Cool_J" "Schoolly_D" "Ice-T" "Snoop_Dogg"
                "Tupac_Shakur" "Dr._Dre" "Masta_Ace" "Public_Enemy"
                "Wu-Tang_Clan" "Notorious_B.I.G." "Eminem" "T.I." "50_Cent"])

(defn add-sites
  "Adds a collection of sites with an optional `prefix` and `suffix`
  concatenated to the urls in `coll`"
  ([category coll] (doseq [url coll] (add-site category url)) @database)
  ([category prefix coll] (add-sites category (map #(str prefix %) coll)))
  ([category prefix coll suffix] (add-sites category
                                            (map #(str prefix % suffix)
                                                 coll))))

(defn add-presets
  ([]
     (add-sites "Countries" wikipedia countries)
     (add-sites "Rock Music" wikipedia rock-music)
     (add-sites "Rap Music" wikipedia rap-music)))

