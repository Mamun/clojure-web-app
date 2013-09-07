(ns zme.app-service.upload-handler
  (:use
   [noir.request]
   [compojure.core])
  (:require
   (ring.middleware [multipart-params :as mp])
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [compojure.route :as route]
   [net.cgrand.enlive-html :as html]
   [zme.modules.common-view :as common]))



(html/defsnippet upload-snippet "public/partials/upload.html" [:#main-content]
  []
  [:form] (html/set-attr :action "/upload/file"))


(defn render [content ]
  (apply str (common/base
              {:title "Upload"
               :menu (common/menu-snippet "Upload")
               :body content})))


(defn upload-file [file]
  (let [file-name (:filename file)
        size (:size file)]
   ; (io/copy (:tempfile file)  (format "/tmp/%s" file-name))
    (render (html/html [:h1 file-name]
                       [:h1 size]))))


(defn handler [app-context]
  (let [r  (routes
            (GET "/" [:as r]
                 (render (upload-snippet) ))
            (mp/wrap-multipart-params
             (POST "/file" {params :params}
                   (let [file (:file params )]
                     (log/info params)
                     (upload-file file))))) ]
    [(context "/upload" []  r)] ))
