(defproject webcat "0.1.0"
  :description "CSC365 Assignment 1: Website categorization program."
  :url "https://github.com/Rosnec/webcat"
  :license {:name "GNU General Public License version 3"
            :url "https://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [me.raynes/laser "1.1.1"]]
  :main webcat.core
  :aot [webcat.core]
  :java-source-paths ["src/util/java"])
