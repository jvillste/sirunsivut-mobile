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
   #_[:pre (prn-str (take 10 (:teasers state)))]
   (for [[index teaser] (map-indexed vector (take 10 (:teasers state)))]
     [:div {:key (hash teaser)
            :style {:margin-top "20px"}}
      
      [:a {:href (str "#/node/" (:nid teaser))
           :class [:page_list_header]}
       (:title teaser)]
      
      [:div {:class [:page_list_date]
             :style {:margin-left "10px"
                     :display "inline"}}
       (cljs-time.format/unparse (cljs-time.format/formatter "dd.MM.YYYY")
                                 (cljs-time.coerce/from-long (* 1000 (long (:created teaser))))) ]
      
      [:a {:href (str "#/node/" (:nid teaser))
           :class [:teaser]}
       [:div {:dangerouslySetInnerHTML {:__html (:teaser teaser)}}]]])])

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

    (call [:menu]
          (fn [menu]
            (swap! state-atom assoc :menu menu)))

    state-atom))

(defn leaf-menu [menu]
  [:li {:key (hash menu)}
   [:a {:href (if-let [node (:node menu)]
                (str "#/node/" node)
                "#")}
    (:title menu)]])

(defn drop-down-menu [menu]
  [:li {:class [:dropdown]}
   [:a {:href (:link menu) :class [:dropdown-toggle] :data-toggle "dropdown" :role "button" :aria-haspopup "true" :aria-expanded "false"}
    (:title menu) [:span {:class [:caret]}]]
   [:ul {:class "dropdown-menu"}
    (for [child (:children menu)]
      (leaf-menu child))]])

(defn navbar [menus]
  [:nav {:class "navbar navbar-default"}
   [:div {:class [:container-fluid]}
    [:div {:class [:navbar-header]}
     [:button {:type "button" :class "navbar-toggle collapsed" :data-toggle "collapse"
               :data-target "#top-navigation" :aria-expanded "false"}
      [:span {:class [:sr-only]} "toggle navigation"]
      [:span {:class [:icon-bar]}]
      [:span {:class [:icon-bar]}]
      [:span {:class [:icon-bar]}]]
     [:a {:class [:navbar-brand] :href "#"}
      "Sirpa Kauppinen"]]

    [:div {:class "collapse navbar-collapse" :id "top-navigation"}
     [:ul {:class "nav navbar-nav navbar-right"}
      (for [menu menus]
        (if (empty? (:children menu))
          (leaf-menu menu)
          (drop-down-menu menu)))]]]])

(defn page [state-atom]
  (let [state @state-atom]
    [:div
     [navbar (:menu state) #_[{:title "Blogi"
                               :link "#/blogi"
                               :children [{:title "Asia 1" :link "#/blogi/1"}
                                          {:title "Asia 2" :link "#/blogi/1"}]}]]
     [:div {:style {:margin-left "10px" :margin-right "10px"}}
      (if (:node state)
        [node (:node state)]
        [teaser-list state])]
     
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
  
  #_(aset js/document "body" "onresize" choose-layout)
  (choose-layout)
  
  #_(println (aset js/document "body" "onresize" choose-layout))
  (reagent/render-component [page state-atom]
                            #_(.-body js/document)
                            (.getElementById js/document "app"))
  

  #_(reagent/render-component [foo]
                              (.-body js/document)))


