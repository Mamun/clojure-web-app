(comment (ns zme.hremote
    (:use [wremote.core]))

(defremote ping [value]
  (do
    (println value)
    (str "Hello " value)))

)





;(ns zme-web.hremote)
;(println "hremoting load")
