(defproject sirunsivut-mobile "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [reagent "0.5.1"]
                 [org.clojure/core.async "0.2.374"]
                 [secretary "1.2.3"]
                 [cor "0.1.0-SNAPSHOT"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target" 
                                    "resources/public/css/compiled"]

  :figwheel {:css-dirs ["resources/public/css"]
             :server-port 3450}

  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.0-1"]]
                   :source-paths ["src/cljs"] }}
  
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"
                                       "checkouts/cor/src"
                                       "settings/dev"]
                        :figwheel {:on-jsload "sirunsivut-mobile.core/main"}
                        :compiler {:main sirunsivut-mobile.core
                                   ;; :output-to "../sirunsivut-backend/resources/public/js/compiled/app.js"
                                   :output-to "../sirunsivut/sites/all/themes/siru/javascript/compiled/app.js"
                                   ;; :output-dir "../sirunsivut-backend/resources/public/js/compiled/out"
                                   :output-dir "../sirunsivut/sites/all/themes/siru/javascript/compiled/out"
                                   ;; :asset-path "js/compiled/out"
                                   :asset-path "/sites/all/themes/siru/javascript/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main sirunsivut-mobile.core
                                   :output-to "../sirunsivut-backend/resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})
