(ns webcat.dat
  "Maintains database of URL -> category mappings")

(defrecord Category [name occurences words pages])

(defrecord Page [url occurences words])

; Mapping of :category -> :word-structure
(def database
  (ref {}))

(defn serialize
  "Serializes the database for saving to disc"
  ([] nil))

(defn deserialize
  "Deserializes a database saved to disc"
  ([serialized] nil))

(defn load-backup
  "Load a serialized database into the database"
  ([serialized]
     (let [backup (deserialize serialized)]
       (dosync (ref-set database backup)))))
