(ns zme.modules.user-util
  (:use
   [datomic.api :only [q db] :as d])
  (:require
   (cemerick.friend [credentials :as creds])) )

(def VALID-CHARS
  (map char (concat  (range 65 91)
                     (range 97 123))))
(defn random-char []
  (nth VALID-CHARS (rand (count VALID-CHARS))))

(defn random-str [length]
  (apply str (take length (repeatedly random-char))))

(defn get-random-data [total]
  (for [v (range  0 total)]
    (let [s-name (random-str 5)]
      {:db/id (d/tempid :db.part/db )
       :user/s-name s-name
       :user/l-name (random-str 5)
       :user/password (creds/hash-bcrypt s-name)
       :user/status :user.status/active})))

;(println (get-random-data 3))
