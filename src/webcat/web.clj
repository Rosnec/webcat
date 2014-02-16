(ns webcat.web
  (:require [clojure.java.io :as io]
            [clojurewerkz.crawlista.extraction.content
             :refer [extract-text extract-title]]))

(defn url-source
  ([url] (slurp (io/reader url))))

(defn url-title
  ([url] (extract-title (url-source url))))

(defn url-text
  ([url] (extract-text (url-source url))))
