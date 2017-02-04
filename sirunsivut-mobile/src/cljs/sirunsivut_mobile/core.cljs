(ns sirunsivut-mobile.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.test :refer-macros [deftest is testing run-tests]]
            [cljs.core.async :as async]
            [cljs.pprint :as pprint]
            [cljs-http.client :as http]
            [secretary.core :as secretary]
            [cor.db :as db]
            [cor.api :as cor-api]
            [cor.state :as state]
            [cor.data-state :as data-state]
            [cor.debug :as debug]
            [sirunsivut-mobile.settings :as settings])
  (:require-macros [cljs.core.async.macros :as async]))

(enable-console-print!)

(defn page []
  (binding [cor-api/api-url settings/api-url]
    (let [state (reagent/atom {})]
      (cor-api/call [:terms]
                    (fn [terms]
                      (prn terms)
                      (swap! state assoc :terms terms)))
      (fn []
        [:pre "terms4:" #_(.-href (first js/link_data)) (pprint/write (:terms @state)
                                                              :stream nil)]))))

(defn ^:export main []
  (reagent/render-component [page]
                            #_(.-body js/document)
                            (.getElementById js/document "app"))

  #_(reagent/render-component [foo]
                              (.-body js/document)))
