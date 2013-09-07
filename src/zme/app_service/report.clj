(ns zme.app-service.report
  (:use [compojure.core]
        [compojure.response]
        [incanter core stats charts])
  (:require  [ring.util.response :as resp]
             [zme.modules.common-view :as common]
             [net.cgrand.enlive-html :as html])
  (:import (java.io ByteArrayOutputStream
                    ByteArrayInputStream)))


(defn gen-samp-hist-png
  [request size-str mean-str sd-str]
  (let [size (if (nil? size-str)
               1000
               (Integer/parseInt size-str))
        m (if (nil? mean-str)
            0
            (Double/parseDouble mean-str))
        s (if (nil? sd-str)
            1
            (Double/parseDouble sd-str))
        samp (sample-normal size
                            :mean m
                            :sd s)
        chart (histogram
               samp
               :title "Normal Sample"
               :x-label (str "sample-size = " size
                             ", mean = " m
                             ", sd = " s))
        out-stream (ByteArrayOutputStream.)
        in-stream (do
                    (save chart out-stream)
                    (ByteArrayInputStream.
                     (.toByteArray out-stream)))]
    (->
     in-stream
     (resp/response)
     (resp/content-type "image/png"))))



(defn index! [r]
  (apply str (common/base
              {:title "Report"
               :menu (common/menu-snippet "Report")
               :body (html/html [:img {:src "/report/img?size=500&mean=100&sd=10"   }] )})))


(defn handler [app-context]
  (let [r  (routes
            (GET "/" [:as r ] (index! r) )
            (GET "/ping3" [:as r] (resp/response  "Ping success"))
            (GET "/img" [size mean sd :as r]
                 (gen-samp-hist-png r size mean sd)))]
    [(context "/report" []  r)]))
