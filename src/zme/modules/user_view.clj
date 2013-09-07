(ns zme.modules.user-view
  (:require [hiccup.element :as e]
            [net.cgrand.enlive-html :as html]
            [zme.modules.common-view :as common]))


(defn render [{:keys [type title data content]}]
  (cond
   (= type "partials")  (apply str (html/emit*  content))
   :else
   (apply str (common/base
               {:title title
                :menu (common/menu-snippet "User")
                :body content}))))


(def ^:dynamic *main-selector* [:body :div#main-content])

(html/defsnippet user-edit "public/partials/user.edit.html" *main-selector*
  [r params error-params ]
  [:form] (html/do->
           (html/set-attr :action (r :save!)))

  [:form :div#s-name :input] (html/set-attr :value (:s-name params))
  [:form :div#s-name]
  (html/add-class (if (empty? (:s-name error-params))
                    "control-group"
                    "control-group error"))
  [:form :div#s-name :span]
  (html/do->
   (html/add-class (if-not (empty? (:s-name error-params))
                     "help-inline" ))
   (html/append (if-not (empty? (:s-name error-params))
                  (first (:s-name error-params))  )) )


  [:form :div#l-name :input] (html/set-attr :value (:l-name params))
  [:form :div#l-name]
  (html/add-class (if (empty? (:s-name error-params))
                    "control-group"
                    "control-group error"))
  [:form :div#l-name :span]
  (html/do->
   (html/add-class (if-not (empty? (:l-name error-params))
                     "help-inline" ))
   (html/append (if-not (empty? (:l-name error-params))
                  (first (:l-name error-params))  )) ) )


(html/defsnippet user-details "public/partials/user.details.html" *main-selector*
  [r content]
  [:#main-content html/any-node]
  (html/replace-vars {:s-name (:s-name content)
                      :l-name (:l-name content)
                      :update-url (r :update (:id content) )
                      :delete-url (r :delete! (:id content) )
                      :list-url (r :index )} ) )


(defn pagination  [routes page size ]
  (let [page (or page 0)
        t-page 6
        start (cond
               (= 0 (rem page t-page)) (- page t-page)
               (= 1 (rem page t-page)) (- page 1)
               :else (*  (quot page t-page ) t-page ))
        end (+ start t-page  1)]
    [:div.pagination.pagination-right
     [:ul
      (if (= 0 start )
        [:li  {:class "disabled"}
         [:a {:href "#" } "<<"]]
        [:li  [:a {:href (str  (routes :index ) "?page=" start )  } "<<"]])

      (map
       (fn [v]
         [:li (if (=  v page) {:class  "active"})
          [:a {:href (str (routes  :index ) "?page=" v )  } v ]])
       (range (+ 1 start) end ))
      [:li [:a {:href (str (routes :index ) "?page=" end  )   } ">>"]]]]))


(defn index [routes rows page size]
  (let [*routes* (routes :partials-index!)
        js-fn (str "zme.user.list_view(\"" *routes* "\");" ) ]
    (html/html
     [:div#main-content
      #_[:h3 "List view"]
      [:table.table.table-hover.table-condensed
       [:thead [:tr [:th "First Name"] [:th "Last Name"] [:th "Action"]]]
       [:tbody (map
                (fn[row] [:tr
                         [:td (:s-name row) ]
                         [:td (:l-name row) ]
                         [:td (e/link-to {:class "btn btn-mini btn-primary"} (routes :details! (:id row) )  "Details")]
                         ]) rows) ]]
      (if-not (empty? rows)
        (pagination  routes page size ) )
      (e/link-to {:class "btn btn-primary"} (routes :create!)   "Create")
      (common/js-ready js-fn )])))
