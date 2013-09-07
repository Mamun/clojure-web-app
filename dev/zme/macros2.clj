(ns zme.macros2)

(defmacro defnhello [fn-name & fn-rest]
 `(defn ~fn-name ~@fn-rest))

(defnhello hello [n] (println n))
