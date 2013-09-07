(ns repl
  (:require [clojure.browser.repl :as repl]))


(defn ^:export cljs []
 (repl/connect "http://localhost:9000/repl"))
