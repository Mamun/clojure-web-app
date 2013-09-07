(ns zme.modules.home-handler
  (:use [noir.request]
        [compojure.core] )
  (:require
   [ring.util.response :as resp]
   [net.cgrand.enlive-html :as html]
   [zme.modules.common-view :as common]))


(def ^:dynamic *index-selector* [:body :div#main-content])


(html/defsnippet index-snippet "public/partials/home/index.html" *index-selector*
  []
  [:div#main-content] (html/content (html/html [:h3#hello "Hello world  from index"]))  )


(defn main-content []
  (html/content  (html/html [:h3#hello "Hello worls"])))


(defn index! [app-context r]
  (apply str (common/base
              {:title "Home"
               :menu (common/menu-snippet "Home")
               :body (index-snippet)})))


(defn handler [app-context]
  (let [r (routes
           (GET "/"  [:as r] ( resp/response   (index! app-context r))) )]
    [(context "/home" []  r )]))
