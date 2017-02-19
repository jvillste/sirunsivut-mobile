(ns sirunsivut-backend.api
  (:require [clojure.data :as data]
            [sirunsivut-backend.db :as db]
            [clj-time.coerce :as clj-time-coerce]
            [clojure.java.jdbc :as jdbc])
  [:use clojure.test])

(defn ^:cor.api/stateles foo [x] x)

(def db-url "jdbc:mariadb://localhost:3306/drupal?user=drupal&password=drupal")

(defn query [query-map]
  (db/query db-url
            query-map))

(defn bytes-to-megabytes [bytes]
  (int (/ bytes 1024 1024)))

(defn memory-usage []
  (let [runtime (Runtime/getRuntime)]
    {:total (bytes-to-megabytes (.totalMemory runtime))
     :free (bytes-to-megabytes (.freeMemory runtime))
     :max (bytes-to-megabytes (.maxMemory runtime))
     :used (bytes-to-megabytes (- (.totalMemory runtime)
                                  (.freeMemory runtime)))}))
(memory-usage)
#_(System/gc)
#_(doseq [result (take 10 (filter (fn [node] (.contains (:title node) "Avoimuutta"))
                                  (db/query db-url
                                            {:select [:*] :from [:node]})))]
    (prn (:title result) (clj-time-coerce/from-long (* 1000 (long (:created result))))))

(defn long-to-time [long-time]
  (clj-time-coerce/from-long (* 1000 (long long-time))))

(comment
  (db/query db-url
            {:select [:*] :from [:node_revisions]
             :where [:= :nid 834]}))

(defn ^:cor.api/stateles terms []
  (query {:select [:term_data.tid :term_data.name] :from [:term_data]
          :where [:= :term_data.vid 2]}))

(comment (terms))

(defn ^:cor.api/stateles teasers [tid]
  (if tid
    (query {:select #_[:*] [:node_revisions.title :node_revisions.teaser :node.created :node.nid]
            :from [:term_node]
            :left-join [:node [:= :node.nid :term_node.nid]
                        :node_revisions [:= :node_revisions.vid :node.vid]]
            :where [:and
                    [:= :term_node.tid tid]
                    [:= :node.status 1]
                    [:= :node.type "story"]
                    #_[:= :node.promote 1]
                    #_[:like :node_revisions.title "Avoimuutta pak%"]]
            :order-by [[:node.created :desc]]})
    (query {:select #_[:*] [:node_revisions.title :node_revisions.teaser :node.created :node.nid]
            :from [:node]
            :left-join [:node_revisions [:= :node_revisions.vid :node.vid]]
            :where [:and
                    [:= :node.status 1]
                    [:= :node.type "story"]
                    #_[:= :node.promote 1]]
            :order-by [[:node.created :desc]]})))


(comment
  (->> (teasers nil)
       (map (fn [node] (dissoc node :body)) )
       (take 10)
       #_(filter (fn [teaser]
                 (.startsWith (:title teaser) "Kou" ))))

  (clojure.data/diff {:tid 53,
                      :format 2,
                      :moderate 0,
                      :vid 1024,
                      :title_2 "Koulujen oireilmoitusten määrä",
                      :uid 1,
                      :nid 1016,
                      :type "story",
                      :created 1477904505,
                      :nid_2 1016,
                      :title "Koulujen oireilmoitusten määrä",
                      :tnid 0,
                      :sticky 0,
                      :status 1,
                      :language "",
                      :comment 2,
                      :changed 1478114134,
                      :vid_2 1024,
                      :nid_3 1016,
                      :translate 0,
                      :uid_2 1,
                      :timestamp 1478114134,
                      :vid_3 1024,
                      :teaser "<p>Tässä koulukohaiset oireilmoitusmäärät. Hämeenkylän koulun mittaisia ongelmakouluja on useita.",
                      :log "",
                      :promote 1}
                     {:tid 39,
                      :format 2,
                      :moderate 0,
                      :vid 1024,
                      :title_2 "Koulujen oireilmoitusten määrä",
                      :uid 1,
                      :nid 1016,
                      :type "story",
                      :created 1477904505,
                      :nid_2 1016,
                      :title "Koulujen oireilmoitusten määrä",
                      :tnid 0,
                      :sticky 0,
                      :status 1,
                      :language "",
                      :comment 2,
                      :changed 1478114134,
                      :vid_2 1024,
                      :nid_3 1016,
                      :translate 0,
                      :uid_2 1,
                      :timestamp 1478114134,
                      :vid_3 1024,
                      :teaser "<p>Tässä koulukohaiset oireilmoitusmäärät. Hämeenkylän koulun mittaisia ongelmakouluja on useita.",
                      :log "",
                      :promote 1}))

(defn ^:cor.api/stateles node [nid]
  (first (query {:select #_[:*] [:node_revisions.title :node_revisions.teaser :node_revisions.body :node.created]
                 :from [:node]
                 :left-join [:node_revisions [:= :node_revisions.vid :node.vid]]
                 :where [:= :node.nid nid]})))
(comment
  (node 75))

(defn left-menu [parent-id]
  (->> (query {:select #_[:*] [:link_path :link_title :p2]
               :from [:menu_links]
               :where [:and
                       [:= "primary-links" :menu_links.menu_name]
                       [:not [:= 1 :menu_links.hidden]]
                       [:not [:= 0 :menu_links.p2]]
                       [:= :menu_links.p1 parent-id]]})
       #_(filter (fn [link]
                   true
                   #_(.contains (:options link) "langcode\";s:2:\"fi\";}")
                   
                   #_(.startsWith (:link_title link) "Vaalit" )))))

(comment

  (left-menu 310))

(defn node-from-url [url]
  (if-let [id (second (re-matches #"node/(\d*)" url))]
    (read-string id)
    nil))

(deftest node-from-url-test
  (is (= 74
         (node-from-url "node/74"))))

(defn drupal-menu-to-menu [drupal-menu]
  (-> drupal-menu
      (dissoc #_:p1 :p2 :link_title :link_path)
      (assoc :title (:link_title drupal-menu)
             :node (node-from-url (:link_path drupal-menu)))))

(defn ^:cor.api/stateles menu []
  (->> (query {:select [:*]
               :from [:menu_links]
               :where [:and
                       [:= "primary-links" :menu_links.menu_name]
                       [:not [:= 1 :menu_links.hidden]]
                       [:= 1 :menu_links.depth]]})
       (filter (fn [link]
                 (.contains (:options link) "langcode\";s:2:\"fi\";}")
                 
                 #_(.startsWith (:link_title link) "Tue Sirua" )))
       (map (fn [link]
              (-> link
                  (select-keys [:p1 :link_title :link_path])
                  (assoc :children (map drupal-menu-to-menu (left-menu (:p1 link)))))))
       (map drupal-menu-to-menu)))


(comment
  (menu)
  (left-menu 310))


;; 310 311

#_{:plid 0,
   :p2 0,
   :p4 0,
   :p3 0,
   :link_path "node/704",
   :updated 0,
   :p5 0,
   :p8 0,
   :external 0,
   :p9 0,
   :menu_name "primary-links",
   :has_children 1,
   :p1 310,
   :module "menu",
   :router_path "node/%",
   :p7 0,
   :weight -43,
   :hidden 0,
   :customized 1,
   :link_title "Tue Sirua",
   :mlid 310,
   :depth 1,
   :options "a:3:{s:10:\"attributes\";a:1:{s:5:\"title\";s:7:\"Tauluja\";}s:5:\"alter\";b:1;s:8:\"langcode\";s:2:\"fi\";}",
   :expanded 0,
   :p6 0}

#_{:plid 310,
   :p2 153,
   :p4 0,
   :p3 0,
   :link_path "node/76",
   :updated 0,
   :p5 0,
   :p8 0,
   :external 0,
   :p9 0,
   :menu_name "primary-links",
   :has_children 0,
   :p1 310,
   :module "menu",
   :router_path "node/%",
   :p7 0,
   :weight -49,
   :hidden 0,
   :customized 1,
   :link_title "Vaalit 2011",
   :mlid 153,
   :depth 2,
   :options "a:2:{s:10:\"attributes\";a:1:{s:5:\"title\";s:8:\"Rahoitus\";}s:5:\"alter\";b:1;}",
   :expanded 0,
   :p6 0}


