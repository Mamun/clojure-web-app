(ns zme.modules.common-view
  (:require [hiccup.core :as h]
            [hiccup.page :as page]
            [hiccup.element :as e]
            [hiccup.form :as form]
            [net.cgrand.enlive-html :as html]))

(def ^:dynamic *context* "")

(defn with-context [param]
  (str *context* param))


(defn js-ready [& name]
  (e/javascript-tag (str "$(document ).ready(function() {" (reduce str name) "});" )))


(html/deftemplate base "public/base.template.html" [{:keys [title menu body]}]
  [:head :title] (html/content title)
  [:div#menu-content] (html/substitute menu)
  [:div#main-content] (html/substitute body ))


(def ^:dynamic *menu-selector* [:body :div#menu-content])

(def ^:dynamic *navigation-elements* [{:text "USER INFO" :href "#" :header true}
                                      {:text "User" :href "/user"}
                                      {:text "Upload" :href "/upload"}
                                      {:text "Report" :href "/report"}
                                      {:text "APP INFO" :href "#" :header true}
                                      {:text "Home" :href "/home"}])

(html/defsnippet menu-snippet "public/partials/menu.html" *menu-selector*
  [active ]
  [:ul [:li html/first-of-type]]
  (html/clone-for [{:keys [text href header]} *navigation-elements*]
                  [:li] (html/do->
                         (html/add-class (if header "nav-header"))
                         (html/add-class (if (= text active)
                                           "active")))
                  [:li.nav-header] (html/content text)
                  [:li :a] (html/do->
                            (html/content text)
                            (html/set-attr :href href))))


 ;############################  api test view  ############################

(defn api-template [title & body]
  (h/html
   "<!DOCTYPE html>"
   [:html  [:head
	    [:meta {:charset "utf-8"}]
	    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
	    [:title title]
            (page/include-js (with-context  "/assets/lib/jquery-1.10.0.min.js"))
	    ]
    [:body {:data-spy "scroll"}
     [:div.container
      [:div.row-fluid
       body
       (js-ready "console.log(\"Hello from API test view \")")
       (comment  (e/javascript-tag

                  "$(document ).ready(function() {
          // alert(\"API load done\");
          zme_web.core.update_list();
        });"
                  ))
       ]]
     ]
    (page/include-js (with-context  "/assets/javascripts/zme.js"))]))
