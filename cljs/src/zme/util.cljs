(ns zme.util )

(defn serialize-clj [f]
  (let [rf (fn [data]
             (reduce conj (map (fn [{:keys [name value]}]
                                 {(keyword name) value}) data)) )]
    (fn []
      (this-as this (->
                     (.serializeArray (js/jQuery this))
                     (js->clj :keywordize-keys true)
                     (rf)
                     (f))))))
