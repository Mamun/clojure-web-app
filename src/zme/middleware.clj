(ns zme.middleware
  (:require [clojure.tools.logging :as log]))


(defn log [value]
  (log/info value))

(log/info "hello")

(defn wrap-request-log [app]
  (fn [{:keys [request-method uri] :as req}]
    (let [resp (app req)
          _ (log/info (-> req
                          (dissoc :body)
                          (dissoc :credential-fn)
                          (dissoc :cemerick.friend/auth-config)
                          (dissoc :unauthenticated-handler)
                          (dissoc :headers)))
       ;   _ (log/info resp)
          ]
      resp)))

(defn wrap-exceptions [app]
  (fn [request]
    (try
      (app request)
      (catch Exception ex
        (.printStackTrace ex)
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body "Exception is server"}))))
