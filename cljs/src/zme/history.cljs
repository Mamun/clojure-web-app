(ns zme.history
  (:require [goog.History :as history]
            [goog.history.EventType :as eventtype]) )


(defn enable-history []
  (let [h  (goog.History.)
        handler (fn [e]
                  (do
                    (.preventDefault e)
                    (.log js/console "Navigation pressed")
                    false
                    ))]
    (goog.events/listen h goog.history.EventType/NAVIGATE handler)
    (.setEnabled h true)))

  ;(enable-history)
