(ns sirunsivut-backend.handler
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [cor.api :as cor-api]
            [sirunsivut-backend.api :as api]))


(defn app []
  (apply compojure/routes
         (concat (cor-api/api-routes "/api"
                                     {}
                                     'sirunsivut-backend.api)
                 [(route/resources "/")
                  (route/not-found "Not Found")])))


