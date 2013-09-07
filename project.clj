(def common-deps '[[org.clojure/tools.cli "0.2.1"] ;;Command line interface
                   [metis "0.3.3"] ;validation lib
                   [slingshot "0.10.3"]  ;for throw and catch
                   [org.clojure/java.jdbc "0.2.3"]   ;;Database lib
                   [postgresql "9.1-901.jdbc4"]
                                        ;[com.oracle/ojdbc "1.4"] ;;Database deps
                   [com.datomic/datomic-free "0.8.4020.24" :exclusions [org.slf4j/log4j-over-slf4j
                                                                        org.slf4j/slf4j-nop ]]
                   [korma "0.3.0-beta9"]
                   [sql-finder "1.0.1" :exclusions [midje] ]
                   [ring "1.1.0"]                     ;;Web lib-clojure
                   [ring-server "0.2.8"]
                   [ring/ring-json "0.2.0"]
                   [ring-middleware-format "0.3.0" :exclusions [ring] ]

                   [compojure "1.1.1"]
                   [com.cemerick/friend "0.1.3"  :exclusions [ring] ]
                   [lib-noir "0.6.4"]

                   [enlive "1.1.1"]
                   [hiccup "1.0.0"]

                   [incanter "1.5.2"]

                   [org.clojure/clojure-contrib "1.2.0"]

                   [org.clojure/tools.logging "0.2.6"]
                   [log4j "1.2.15" :exclusions [javax.mail/mail
                                                javax.jms/jms
                                                com.sun.jdmk/jmxtools
                                                com.sun.jmx/jmxri]]
                   [org.slf4j/slf4j-log4j12 "1.6.6"]])


(defproject zme-web "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :repositories { "sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/" }
  :dependencies ~(cons '[org.clojure/clojure "1.5.1"]
                         common-deps)
  :profiles {:production
             {:ring {:open-browser? false, :stacktraces? false, :auto-reload? false}}
             :dev  {:source-paths ["dev"]
                    :dependencies [[com.h2database/h2 "1.3.154"]
                                        ;testing
                                   [ring-mock "0.1.3"]
                                   [ring/ring-devel "1.1.8"]
                                        ;Client side
                                   [org.clojure/clojurescript "0.0-1820"]
                                   [org.clojure/core.async "0.1.0-SNAPSHOT"]
                                   [jayq "2.3.0"]
                                   [prismatic/dommy "0.1.1"]
                                   [com.cemerick/clojurescript.test "0.0.4"]
                                   [com.cemerick/piggieback "0.0.4"]]
                    :plugins [[lein-ring "0.8.3"]
                              [lein-midje "3.0.0"]
                              [lein-cljsbuild "0.3.2"]]}}
  :source-paths ["src"]
  :test-paths ["test"]
  :ring {:handler zme.system/handler
         :init zme.system/init
         :destroy zme.system/destroy }
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild {:crossovers [crossover]
              :crossover-path "cljs/src-crossover"
              :builds {:dev
                       {:source-paths  ["cljs/src" "cljs/src-dev"]
                        :incremental true
                        :jar true
                        :compiler
                        {:warnings true
                         :pretty-print true
                         :output-to "resources/public/assets/javascripts/zme.js"
                         :optimizations :whitespace }}
                       :prod
                       {:source-paths ["cljs/src" ]
                        :compiler
                        {:warnings true
                         :pretty-print false
                         :output-to "resources/public/assets/javascripts/zme.min.js"
                         :optimizations :advanced}}}})
