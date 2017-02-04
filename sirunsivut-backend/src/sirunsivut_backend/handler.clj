(ns sirunsivut-backend.handler
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [cor.api :as cor-api]
            [sirunsivut-backend.api :as api]))


(defn app []
  (-> (cor-api/app {}
                   'sirunsivut-backend.api)
      (cor-api/wrap-cors [#"http://localhost:8000"
                          #"http://sirpakauppinen.fi"]
                         [:post])))


