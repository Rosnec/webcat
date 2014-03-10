(ns webcat.core
  "Launches webcat program."
  (:require [webcat.dat  :refer [add-presets]]
            [webcat.gui  :refer [main-window]]
            [seesaw.core :refer [pack! show!]])
  (:import [util.java PersistentBST])
  (:gen-class))

(defn -main
  "Launch the program."
  [& args]
  (let [bst (PersistentBST/create {1 2 3 4})]
    (println "IT'S WORKING!!!")
    (println "1 ->" (bst 1))
    (comment
      (doseq [k (.keys bst)]
        (println k))))
  (add-presets)
  (-> main-window
      pack!
      show!))
