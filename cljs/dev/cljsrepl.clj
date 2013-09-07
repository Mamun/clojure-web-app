(ns cljsrepl
  (:require [cljs.repl.browser]))

(cemerick.piggieback/cljs-repl
  :repl-env (doto
              (cljs.repl.browser/repl-env :cport 9000)
              (cljs.repl/-setup)))
