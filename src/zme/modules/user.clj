(ns zme.modules.user
  (:use
   [metis.core ]
   [datomic.api :only [q db] :as d])
  (:require
   [clojure.tools.logging :as log]
   (cemerick.friend [credentials :as creds])))



(defvalidator user-validator
  [[:l-name :s-name] :presence {:message "This field is required."} ]
  [:l-name :with {:validator (fn [attr] true) :message "error!"}]
  [[:l-name :s-name] :formatted {:pattern #"[A-Za-z]+" :message "wrong formatting!"}])


(defn from-view-model [{:keys [id s-name l-name]}]
  {:db/id id
   :user/password  (creds/hash-bcrypt s-name)
   :user/l-name l-name
   :user/s-name s-name })


(defn to-view-model [db-entity ]
  {:id (:db/id db-entity)
   :l-name (:user/l-name  db-entity)
   :s-name (:user/s-name  db-entity)
   :password (:user/password db-entity)})


(defn defaults []
  {:s-name "" :l-name "" :id  (second  (second  (d/tempid :db.part/db )))  })


(defn find-by-id [conn id]
  (-> (d/db conn)
   (d/entity  id)
   (d/touch )
   (to-view-model)))


(defn find-by-name [conn name]
  (let [rule '[[(active? ?u)
                [?u :user/status :user.status/active]]]
        q '[:find ?u
            :in $ ?name %
            :where
            [?u :user/s-name ?name]
            (active? ?u)]]
    (if-let [entity-id  (ffirst (d/q q (d/db  conn) name rule) ) ]
      (find-by-id conn entity-id) )))


(defn find-all [conn page size]
  (let [rule '[[(active? ?u)
                [?u :user/status :user.status/active]]]
        q '[:find ?u
            :in $ %
            :where
            [?u :user/s-name]
            (active? ?u)]
        res (d/q q (d/db conn) rule)
        p (partition-all size  res)]
    (->>
     (nth p (- page 1) (last p) )
     (map (fn [r]
            (find-by-id conn (first r)))))))


(defn update [conn params]
  (let [error-param (user-validator params)]
    (if-not (empty? error-param)
      error-param
      (let [update-data [ (->
                           (from-view-model params)
                           (assoc :user/status :user.status/active))]]
        (log/info "Update")
        @(d/transact conn update-data)
        nil))))


(defn delete [conn id]
  (let [data [{:db/id id
               :user/status :user.status/delete}] ]
    @(d/transact conn data )
    nil))


(defn insert-all [conn data]
  (let [data (conj data {:db/id (d/tempid :db.part/db )
                         :user/s-name "admin"
                         :user/l-name "admin"
                         :user/password (creds/hash-bcrypt "admin")
                         :user/status :user.status/active}) ]
    @(d/transact conn data )
    (println "insert data")))


(comment

  (cond
   (empty? (user-validator {:l-name "test" :s-name "td"} ) )   (println "validation sucess")
   :else  (println "validation fail")  )

  (use '[zme.datomic :only [db-conn] :as db])
  (use '[zme.domain.user-util])

  (def conn (db/init-db))

  @(d/transact conn  (get-random-data 2 ) )

  (println (find-by-id (d/connect uri) 4611681620380877870  ))
  (println (find-by-name conn "XPrjK" ))
  (println (find-all (d/db conn) 1 15  ))

  (update conn {:id -1 :s-name "test" :l-name "test"})

  (println (d/q '[:find ?n
                  :where
                  [4611681620380877870 :user/s-name ?n]]
                (d/db (conn))))
  (do
    (delete (d/connect uri) 4611681620380877870   )
    (update (d/connect uri) {:id 4611681620380877869 :s-name "mamun" :l-name "abdullah" })
    (println (find-by-id (d/connect uri) 4611681620380877870  )))

  (println (let [p  (partition-all 6 (range 20))]
             (nth p 2 (last p))))
  )
