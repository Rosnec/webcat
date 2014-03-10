(ns webcat.core
  "Launches webcat program."
  (:require [webcat.dat  :refer [add-presets]]
            [webcat.gui  :refer [main-window]]
            [seesaw.core :refer [pack! show!]])
  (:gen-class))

(defn -main
  "Launch the program."
  ([& args]
     (add-presets)
     (-> main-window
         pack!
         show!)))
