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
            [cor.debug :as debug])
  (:require-macros [cljs.core.async.macros :as async]))

(defn page []
  (let [state (reagent/atom {})]
    (cor-api/call [:terms]
                  (fn [terms]
                    (swap! state assoc :terms terms)))
    (fn []
      
      [:pre "terms:" (pprint/write (:terms @state)
                                   :stream nil)])))

(defn ^:export main []
  (reagent/render-component [page]
                            (.-body js/document))

  #_(reagent/render-component [foo]
                              (.-body js/document)))
