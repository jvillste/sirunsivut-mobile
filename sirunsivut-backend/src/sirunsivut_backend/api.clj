(ns sirunsivut-backend.api
  (:require [sirunsivut-backend.db :as db]
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


(defn ^:cor.api/stateles terms []
  (query {:select [:*] :from [:term_data]
          :where [:= :term_data.vid 2]}))

(defn nodes-for-term []
  {:select [:*] :from [:term_data]
   :where [:= :term_data.vid 2]})

#_(honeysql.core/format (terms))
#_(map :name (query (terms)))
