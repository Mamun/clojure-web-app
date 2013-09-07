(ns zme.modules.auth-handler
  (:use [compojure.core]
        [datomic.api :only [q db] :as d]
        [noir.request])
  (:require
   [clojure.tools.logging :as log]
   [ring.util.response :as resp]
   [cemerick.friend :as friend]
   (cemerick.friend [workflows :as workflows]
                    [credentials :as creds])
   [net.cgrand.enlive-html :as html]
   [hiccup.form :as f]
   [zme.modules.common-view :as common]
   [zme.modules.user :as user]))


(html/deftemplate login-template "public/login.template.html" [title params]
  [:head :title] (html/content title)
  [:div#main-content :form ]
  (html/do->
   (html/set-attr :action "/login")
   (html/set-attr :method "POST"))
  [:div#main-content :form :p]
  (html/do->
   (html/content (if (not-empty  params) "Sorry, UserName or Password is wrong" ))
   (html/set-attr :style (if (not-empty  params)  "color:red" ))))


(defn handler [app-context]
  (let [r  (routes
            (GET "/login" [:as {params :params} ]
                 (if (friend/current-authentication)
                   (resp/redirect "/user")
                   (apply str  (login-template "Login" params))))
            (friend/logout (ANY "/logout" request (resp/redirect "/login"))) )]
    [r]))


(defn auth-validation [app-context]
  (fn [user]
    (when-let [d-user (user/find-by-name (:datomic-conn app-context)  user )]
      {:username (:s-name  d-user)
       :password (:password d-user)})) )


(defn auth-warp [ring-route validation]
  (let [v (partial creds/bcrypt-credential-fn validation) ]
    (friend/authenticate ring-route
                         {:credential-fn v
                          :workflows [(workflows/interactive-form)]
                          :login-uri "/login"
                          :unauthorized-redirect-uri "/login"
                          :default-landing-uri "/user" })))
