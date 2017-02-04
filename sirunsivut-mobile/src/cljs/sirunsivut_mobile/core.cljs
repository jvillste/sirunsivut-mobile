(ns sirunsivut-mobile.core
  (:import goog.History)
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.test :refer-macros [deftest is testing run-tests]]
            [cljs.core.async :as async]
            [cljs.pprint :as pprint]
            [cljs-http.client :as http]
            [secretary.core :as secretary]
            [cor.db :as db]
            [cor.api :as cor-api]
            [cor.state :as state]
            [cor.routing :as routing]
            [cor.data-state :as data-state]
            [cor.debug :as debug]
            [sirunsivut-mobile.settings :as settings]
            cljs-time.coerce
            cljs-time.format

            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs.reader :as reader])
  (:require-macros [cljs.core.async.macros :as async]))

(enable-console-print!)

(defn teaser-list [state]
  [:div
   (for [teaser (take 40 (:teasers state))]
     [:div {:key (:nid teaser) :style {:clear "both" :margin-top "15px"}}
      [:div {:class [:page_list_date]}
       (cljs-time.format/unparse (cljs-time.format/formatter "dd.MM.YYYY")
                                 (cljs-time.coerce/from-long (* 1000 (long (:created teaser))))) ]
      [:div {:class [:page_list_header_div] :style {:clear "both"}}
       [:a {:href (str "#/node/" (:nid teaser)) :class [:page_list_header]}
        (:title teaser)]
       [:a {:href (str "#/node/" (:nid teaser))
            :class [:teaser]}
        [:div {:dangerouslySetInnerHTML {:__html (:teaser teaser)}}]]]])])

(defn node [node]
  [:div
   [:h3  (:title node)]
   [:div {:dangerouslySetInnerHTML {:__html (:body node)}}]])

(defn call [command callback]
  (binding [cor-api/api-url settings/api-url]
    (cor-api/call command
                  callback)))

(defonce state-atom
  (let [state-atom (reagent/atom {})]

    (routing/setup-routes)
    
    (secretary/defroute "/node/:nid" [nid]
      (call [:node (reader/read-string nid)]
            (fn [node]
              (swap! state-atom assoc :node node)))
      #_(swap! state-atom assoc :nid nid))

    (secretary/defroute "/" []
      (swap! state-atom assoc :node nil))

    (call [:terms]
          (fn [terms]
            (swap! state-atom assoc :terms terms)))
    (call [:teasers nil]
          (fn [teasers]
            (swap! state-atom assoc :teasers teasers)))

    state-atom))

(defn page [state-atom]
  (let [state @state-atom]
    [:div
     [:a {:href "/#/"} "Etusivu"]
     (if (:node state)
       [node (:node state)]
       [teaser-list state])
     #_[:pre "terms:" (pprint/write (:nid state) #_(:terms state)
                                    :stream nil)]
     ]))

(defn choose-layout []
  (if (< (aget js/window "innerWidth")
         1000)
    (do (aset (.getElementById js/document "mobile") "style" "display" "block")
        (aset (.getElementById js/document "desktop") "style" "display" "none"))
    (do (aset (.getElementById js/document "mobile") "style" "display" "none")
        (aset (.getElementById js/document "desktop") "style" "display" "block"))))

(defn ^:export main []
  (aset js/document "body" "onresize" choose-layout)
  (choose-layout)
  #_(println (aset js/document "body" "onresize" choose-layout))
  (reagent/render-component [page state-atom]
                            #_(.-body js/document)
                            (.getElementById js/document "app"))
  

  #_(reagent/render-component [foo]
                              (.-body js/document)))


