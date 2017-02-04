(ns sirunsivut-backend.api
  (:require [clojure.data :as data]
            [sirunsivut-backend.db :as db]
            [clj-time.coerce :as clj-time-coerce]
            [clojure.java.jdbc :as jdbc]))

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

(defn ^:cor.api/stateles teasers [tid]
  (query {:select #_[:*] [:node_revisions.title :node_revisions.teaser :node.created :node.nid]
          :from [:term_node]
          :left-join [:node [:= :node.nid :term_node.nid]
                      :node_revisions [:= :node_revisions.vid :node.vid]]
          :where [:and
                  (if tid
                    [:= :term_node.tid tid]
                    :true)
                  [:= :node.status 1]
                  #_[:= :node.promote 1]
                  #_[:like :node_revisions.title "Avoimuutta pak%"]]
          :order-by [[:node.created :desc]]}))

(defn ^:cor.api/stateles node [nid]
  (first (query {:select [:node_revisions.title :node_revisions.teaser :node_revisions.body :node.created]
                 :from [:node]
                 :left-join [:node_revisions [:= :node_revisions.vid :node.vid]]
                 :where [:= :node.nid nid]})))


