(ns zme.handler
  (:use [ring.util.servlet]
        [ring.middleware.multipart-params]
        [ring.middleware.format-params :only [wrap-restful-params]]
        [ring.middleware.format-response :only [wrap-restful-response wrap-json-response]]
        [compojure.core])
  (:require
   [ring.util.response :as resp]
   [noir.session :as sess]
   [noir.util.middleware :as noir-mid]
   [noir.util.route :as noir-route]
   [compojure.route :as route]
   [compojure.response :as response]
   [cemerick.friend :as friend]
   [zme.modules.auth-handler :as auth]
   [zme.modules.user-handler :as user]
   [zme.modules.home-handler :as home]
   [zme.app-service.upload-handler :as upload]
   [zme.app-service.report :as report]
   [zme.middleware :as app-mid]
   [zme.modules.common-view :as common]))


(defn dev-routes []
  [(GET "/hello" [] (resp/response "HEllo world"))
   (GET "/ping" [] (resp/response "HEllo world"))
   (GET "/ping2" [] (resp/response "HEllo world"))
   (GET "/ping3" [] (resp/response "HEllo world"))
   (GET "/api"  [:as r] (resp/response (common/api-template "API test" "API test")))])


(defn app-routes [app-context]
  (concat
   (dev-routes)
   (auth/handler app-context)
   (user/handler app-context)
   (report/handler app-context)
   (upload/handler app-context)
   (home/handler app-context)))


(defn handler [app-context]
  (let [v (concat [(route/resources "/")]
                  (app-routes app-context)
                  [(route/not-found "Not Found")])
        r (->
           (apply routes v )
           (auth/auth-warp (auth/auth-validation app-context ))
           (wrap-restful-params)
           (wrap-restful-response)
           (app-mid/wrap-request-log))]
    (noir-mid/war-handler (noir-mid/app-handler
                           [r]
                           :middleware []))))
