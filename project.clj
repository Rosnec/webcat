(defproject webcat "1.0.0"
  :description "CSC365 Assignment 1: Website categorization program."
  :url "https://github.com/Rosnec/webcat"
  :license {:name "GNU General Public License version 3"
            :url "https://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [clojurewerkz/crawlista "1.0.0-alpha18"]
                 [incanter "1.5.4"]
                 [seesaw "1.4.4"]]
  :main webcat.core
  :java-source-paths ["src/util/java"]
  :aot [webcat.core])
