(ns webcat.core
  (:import [util.java.BSTStringCounter])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [tree (new BSTStringCounter)]
    (doseq [s ["apple" "pear" "peach" "lemon" "fish" "quail"]]
      (.add tree s))
    (println (.size tree))))
