(ns sirunsivut-backend.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as honeysql]))


(defn query [connection-uri query-map]
  (jdbc/query {:connection-uri connection-uri}
              (honeysql/format query-map)))

