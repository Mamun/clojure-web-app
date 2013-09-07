(ns zme-web.macros)

(defmacro defn+ [fn-name & fn-tail]
  (let [[params & body] fn-tail]
    `(defn ~fn-name ~params ~@body)))
