(ns sirunsivut-backend.main
  (:require [org.httpkit.server :as http-kit]
            [sirunsivut-backend.handler :as handler]
            [taoensso.timbre :as timbre])
  (:gen-class))

(defn -main [& [port]]
  (timbre/info "starting")
  (http-kit/run-server (handler/app)
                       {:port (Integer. (or port
                                            3002))}))

;; development

(def server (atom nil))


(defn start []
  (timbre/info "starting")
  (when @server (@server))
  (.start (Thread. (fn [] (reset! server
                                  (http-kit/run-server (handler/app)
                                                       {:port 3002}))))))
