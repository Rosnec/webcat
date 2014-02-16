(ns webcat.cat
  "Document categorization utilities."
  (:require [webcat.dat :as dat]))


(defn set-categories
  "Sets the current categories to the alternating sequence keyval, where
  the keys are categories and the values are urls.
  To start with, only allow a single URL for each category, but eventually
  create a sequence of URLs for each category, and define a normalization
  function, so they can be combined into a single tree. The individual trees
  are still saved post-normalization, so the sites do not need to be re-scraped
  every time a new site is added to the category."
  ([keyvals] nil))

(defn add
  "Add a site to the category database."
  ([keyval] nil))

(defn clear
  "Clear website database"
  ([] nil))

(defn refresh
  "Refresh website database"
  ([] nil))

(defn remove
  "Remove a site from the category database"
  ([key] nil))
