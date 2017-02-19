(ns sirunsivut-backend.handler
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [cor.api :as cor-api]
            [sirunsivut-backend.api :as api]
            [ring.middleware.cors :refer [wrap-cors]]))

(compojure/defroutes app-routes
  (compojure/GET "/" [] "Jee"))

(defn app []
  #_(wrap-cors app-routes
               :access-control-allow-origin #"http://localhost:8000"
               :access-control-allow-methods [:get :put :post :delete])
  
  (-> (cor-api/app {}
                   'sirunsivut-backend.api)
      
      (wrap-cors :access-control-allow-origin #".*"
                 :access-control-allow-methods [:get :put :post :delete])
      #_(cor-api/wrap-cors [#"http://localhost:8000"]
                           [:post :get])))


