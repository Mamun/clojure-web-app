(ns zme.user
  (:use [jayq.core :only [$]])
  (:use-macros  [jayq.macros :only [let-ajax ready]])
  (:require-macros  [cljs.core.async.macros :refer [go alt!]] )
  (:require
   [goog.Uri :as uri]
   [goog.net.XhrIo :as xhr]
   [cljs.core.async :as async :refer [chan close! put! ]]
   [jayq.core :refer [$ append ajax inner $deferred when done resolve pipe on]  :as jq ]
   [zme.util :as util]))


(defn render-list [context-path rc]
  (go
   (let [uri (goog.Uri. (-> js/window (.-location) (.-href))) ]
     (.setPath uri context-path)
     (.setParameterValue uri "page" (<! rc))
     (let-ajax [v {:url uri :dataType "html" } ]
               (.html (.parent (js/jQuery "#main-content"))  v )))))


(defn add-event-for-pagi []
  (let [rc (chan 1)]
    (->
     (js/jQuery "#main-content .pagination a")
     (.on "click" (fn [event]
                    (jq/prevent event)
                    (this-as t
                             (when-let [page (->
                                              (.attr (js/jQuery t) "href")
                                              (goog.Uri. )
                                              (.getParameterValue "page"))]
                               (put! rc page)
                               (close! rc))))))
    rc))


(defn ^:export list-view [context-path]
  (render-list context-path (add-event-for-pagi)))

 (comment
  "Testing code"
  (let [rc (chan 1)]
    (go
     (<! (async/timeout 2000))
     (>! rc "6"))
    (render-list "/user/snippet/" rc ) )

  (if-let [a 0]
    (println "nil"))

  (js/alert "Hello from repl"))
