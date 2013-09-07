(ns zme.datomic
  (:use [datomic.api :only [q db] :as d]))


(def uri "datomic:mem://zme")

(def schema-tx (read-string (slurp "src/zme/schema.edn")))

(defn init-db
  ([] (init-db uri schema-tx))
  ([uri schema-tx]
     (when (d/create-database uri)
       @(d/transact (d/connect uri) schema-tx))
     (d/connect uri)))


(defn delete []
  (d/delete-database uri))
