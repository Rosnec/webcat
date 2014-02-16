(ns webcat.core
  "Launches webcat program."
  (:require [webcat.gui  :refer [main-window]]
            [seesaw.core :refer [pack! show!]])
  (:gen-class))

(defn -main
  "Launch the program."
  [& args]
  (-> main-window
      pack!
      show!))
