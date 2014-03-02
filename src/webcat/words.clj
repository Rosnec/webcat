(ns webcat.words
  (:require [clojure.string :as string]
            [webcat.util :as util]))

(defn split-words
  "Split a string `s` into a sequence of words."
  ([s] (string/split s #"\s+")))

(defn word-frequencies
  "Create a mapping of word -> word-count for each word in the String `s`.

  Optionally, provide a predicate function `pred` to filter the words by
  before counting their frequencies."
  ([s] (word-frequencies s identity (constantly true)))
  ([s func] (word-frequencies s func (constantly true)))
  ([s func pred] (frequencies (map func
                                   (filter pred
                                           (string/split s #"\s+"))))))

(defn word-proportions
  "Create a mapping of word -> proportion-of-word for each word in `s`.
  proportion-of-word is defined as the frequency of word in parts per `n`
  of the entire word-frequencies map."
  ([s] (word-proportions s 100 identity (constantly true)))
  ([s n] (word-proportions s n identity (constantly true)))
  ([s n func] (word-proportions s n func (constantly true)))
  ([s n func pred] (let [word-freqs (word-frequencies s func pred)]
                     (util/proportions word-freqs n))))
