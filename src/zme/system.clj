(ns zme.system
  (:require [zme.handler :as h]
            [zme.datomic :as d]
            [zme.modules.user :as u]
            [zme.modules.user-util :as u-util]))


(defn system []
  "Returns a new instance of the whole application."
  (let [d-conn (d/init-db)
        c {:datomic-conn d-conn}
        c (assoc c :handler (h/handler c))]
    (u/insert-all d-conn (u-util/get-random-data 25 ))
    (println (str "Total data" (count (u/find-all d-conn 1 15 ))))
    c))


(defonce handler nil)


(defn init
  "init will be called once when app start"
  ([] (init (system)))
  ([s]
     (alter-var-root #'handler (constantly (:handler s)))
     (println "App start successfully")))


(defn stop
  "destroy will be called when your application
shuts down, put any clean up code here"
  []
  (println "shutting down...")
  #_(d/delete))
