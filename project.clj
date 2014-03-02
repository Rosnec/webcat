(defproject webcat "0.2.0-SNAPSHOT"
  :description "CSC365 Assignment 1: Website categorization program."
  :url "https://github.com/Rosnec/webcat"
  :license {:name "GNU General Public License version 3"
            :url "https://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.incubator "0.1.3"]
                 [seesaw "1.4.4"]
                 [clojurewerkz/crawlista "1.0.0-alpha18"]]
  :main webcat.core
  :aot [webcat.core])
