(ns webcat.dat
  "Maintains database of category -> URL -> web page content mappings"
  (:require [clojure.set :refer [intersection]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [webcat.math :as math]
            [webcat.util :as util]
            [webcat.stop :as stop]
            [webcat.web :as web]
            [webcat.words :as words])
  (:import [java.io PushbackReader]))

(defn word-filter
  "Filter to be used on the word lists from the websites. Returns
  logical true if the word should be included, and false if it should not.

  Discards words with length less than 5, or containing non-alphabetic
  characters."
  ([word] (not (or (stop/words word)
                   (re-find #"[^a-zA-Z]"
                            word)))))

(defn make-page
  "Creates a new page entry using the words parsed from the given `url`,
  filtered with the predicate function `pred`."
  ([url] (make-page url string/lower-case word-filter))
  ([url func] (make-page url func word-filter))
  ([url func pred] (into (sorted-map)
                         (words/word-scores (web/url-text url)
                                            1000
                                            func
                                            pred))))

(defonce database
  (ref {}))

(defn add-site
  "Adds `category` -> `url` -> page record for `url` to the database, creating
  any missing fields, and overwriting duplicate urls."
  ([category url] (dosync
                   (alter database assoc-in [category url]
                          (make-page url)))))

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

(defn compare-url
  "Compares the words from the webpage at `url` to the given word map, using
  the top `n` word matches, or top 10 words if `n` is not provided."
  ([url word-map] (let [url-map (make-page url)]
                      (math/compare-words url-map word-map))))

(defn closest-url
  ""
  ([url] (let [site (make-page url)
               best-matches (for [[category sites] @database]
                              (math/best-match site sites))]
           (println "Category Bests:" best-matches "\n")
           (reduce (fn [[r-key r-val] [key val]] (if (< r-val val)
                                        [r-key r-val]
                                          [key   val]))
                   best-matches))))

(defn closest-category
  ""
  ([url] (let [site (make-page url)
               category-averages (reduce (fn [r [category sites]]
                                           (assoc r category
                                                  (math/mean-distance site
                                                                      sites
                                                                      10)))
                                         {} @database)]
           (println "Category Average:" category-averages "\n-----")
           (apply min-key category-averages (keys category-averages)))))


(defn save-backup
  "Save the database to a file."
  ([file] (spit file (str @database))))

(defn load-backup
  "Load the database from file, clearing the existing one."
  ([file] (let [backup (with-open [rdr (io/reader file)]
                             (read (PushbackReader. rdr)))]
            (dosync (ref-set database backup)))))


1
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
(def programming-languages
  ["Bash_(Unix_shell)" "BASIC" "COBOL" "Common_Lisp"
   "Fortran" "Go_(programming_language)" "Haskell_(programming_language)"
   "C_(programming_language)"
   "C%2B%2B" "Clojure" "Java" "JavaScript"
   "Python_(programming_language)" "PHP" "Perl" "Ruby_(programming_language)"
   ])
(def italian-food
  ["Penne" "Maccheroni" "Spaghetti" "Linguine"
   "Prosciutto" "Lasagne" "Salami" "Parmigiano-Reggiano"
   "Pizza" "Gnocchi" "Ravioli"
   "Fettucine_Alfredo" "Mozzarella" "Biscotti"])
(def physics
  ["Thermodynamics" "Quantum_mechanics" "Electromagnetism" "Special_relativity"
   "General_relativity" "Classical_mechanics" "Gravitation" "Astrophysics"
   "Higgs_boson" "Standard_model" "Quantum_field_theory" "Cosmology"
   "Optics" "Fluid_mechanics" "Atomic_nuclei" "Plasma_(physics)"
   ])

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
     (add-sites "Programming Languages" wikipedia programming-languages)
     (add-sites "Italian Food" wikipedia italian-food)
     (add-sites "Physics" wikipedia physics)))

