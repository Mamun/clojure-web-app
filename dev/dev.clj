(ns dev
  (:use
   [ring.server.standalone]
   [clojure.pprint :only [print-table]])
  (:require [zme.system :as sys]
            [zme.handler :as h]))


(defn server-start [ & [port] ]
  (let [port (if port (Integer/parseInt port) 8080)
        server (serve (var sys/handler)
                      {:port port
                       :auto-reload? true
                       :join true
                       :open-browser? false})]
    (println (str "You can view the site at http://localhost:" port))
    server ))


(defonce the-system nil)


(defn start []
  "used for starting the server in development mode from REPL"
  (when (nil? the-system)
    (alter-var-root #'the-system (constantly (sys/system))))

  (if (nil? (get the-system :server))
    (alter-var-root #'the-system
                    (fn [s]
                      (sys/init s)
                      (assoc s :server (server-start ))))
    (alter-var-root #'the-system
                    (fn [s]
                      (let [new-s (assoc s :handler (h/handler s) ) ]
                        (sys/init new-s)
                        new-s)))))


(defn stop []
  (alter-var-root #'the-system
                  (fn [s] (when s
                           (sys/stop)
                           (.stop (:server s))
                           (dissoc s :server)))))
