(ns zme.modules.user-handler
  (:use
   [compojure.core]
   [noir.request])
  (:require
   [clojure.tools.logging :as log]
   [ring.middleware.json :as json]
   [ring.util.response :as resp]
   [cemerick.friend :as friend]
   [zme.modules.user :as u]
   [zme.modules.user-view :as v]))


(defn- action* [context-path type & v]
  (let[m {:index! "/"
          :create! "/create"
          :details! "/"
          :update  "/update/"
          :save! "/update"
          :delete! "/delete/"
          :partials-index! "/partials/"}]
    (str (:context *request*) context-path  (get m type) (reduce str v)) ) )

(def action (partial action* "/user" ))

(defn- index!
  ([app-context r] (index! app-context r "default" ))
  ([app-context r type]
     (let [size 15
           page (Integer/parseInt  (or (get-in r [:params :page] ) "1" ))
           data (u/find-all (:datomic-conn app-context) page size)]
       (cond
        (= type "data") (resp/response data)
        :else (-> {:title "User list"
                   :type type
                   :data data
                   :content (v/index action data page size ) }
                  (v/render)
                  (resp/response))))))


(defn- edit-or-create!
  ([app-context r] (edit-or-create! app-context r nil))
  ([app-context r id]
     (let [data (if (nil? id)
                  (u/defaults)
                  (u/find-by-id (:datomic-conn app-context) (read-string  id)))]
       (cond
        (= type "data") (resp/response data)
        :else  (-> {:title "User Edit"
                    :type "default"
                    :data data
                    :content (v/user-edit action data nil)}
                   (v/render)
                   (resp/response))))))


(defn- details!
  ([app-context r id ] (details! app-context r id  "default") )
  ([app-context r id type]
     (let [data (u/find-by-id (:datomic-conn app-context) (read-string  id))]
       (cond
        (= type "data") (resp/response data)
        :else  (-> {:title "User Details"
                    :type type
                    :data data
                    :content (v/user-details action data)}
                   (v/render)
                   (resp/response))))))


(defn- save! [app-context r]
  (let [params (get-in r [:params])
        id (get params :id)
        params (assoc  params :id (read-string id))]
    (if-let [error-params (u/update (:datomic-conn app-context)  params)]
      (-> {:title "User List"
           :type "default"
           :data nil
           :content (v/user-edit action params error-params ) }
          (v/render)
          (resp/response ))
      (resp/redirect (action :index! )))))


(defn- delete! [app-context r id]
  (u/delete (:datomic-conn app-context)  (read-string id))
  (resp/redirect (action :index! )))


(defn handler [app-context]
  (let [r (routes
           (GET "/"           [:as r] (index! app-context r))
           (GET "/create"     [:as r] (edit-or-create! app-context r ))
           (GET "/:id"        [id :as r] (details! app-context r id  ))
           (GET "/delete/:id" [id :as r] (delete! app-context r id ) )
           (GET "/update/:id" [id :as r ] (edit-or-create! app-context r id) )
           (POST "/update"    [:as r ] (save! app-context r ))
           (context "/partials" []
                    (defroutes u-snippet
                      (GET "/" [:as r] (index! app-context r "partials"))
                      (GET "/:id" [id :as r] (details! app-context r id "partials" ))))
           (context "/data" []
                    (defroutes u-data
                      (GET "/"        [:as r] (index! app-context r "data")  )
                      (GET "/:id"     [id :as r] (details! app-context r id  "data" ))
                      (POST "/update" [:as {params :params}] (resp/response params)))))]
    [(context "/suser" []  r )
     (context "/user" [] (friend/authenticated r))]))
