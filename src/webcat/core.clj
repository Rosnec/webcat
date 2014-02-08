(ns webcat.core
  (:require laser)
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [tree (new BST)
        items ["apple" "pear" "peach" "lemon" "fish" "quail"]]
    (doseq [s items]
      (.add tree s))
    (doseq [s items]
      (println (.contains tree s)))))
