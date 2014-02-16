(ns webcat.words
  (:require [clojure.string :as string]))

(defn split-words
  ([s] (string/split (url-text url) #"\s+")))

(defn word-frequencies
  ([s] (word-frequencies url (constantly true)))
  ([s pred] (frequencies (filter pred
                                 (string/split s #"\s+")))))
