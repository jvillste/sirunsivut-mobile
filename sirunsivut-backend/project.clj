(defproject sirunsivut-backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.mariadb.jdbc/mariadb-java-client "1.5.6"]
                 [clj-time "0.13.0"]
                 [honeysql "0.8.1"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [http-kit "2.1.18"]
                 [compojure "1.5.0"]
                 #_[ring/ring-defaults "0.2.1"]
                 #_[com.taoensso/timbre "4.7.2"]
                 [com.taoensso/timbre "4.5.1"]
                 #_[ring-cors "0.1.8"]
                 [jumblerg/ring.middleware.cors "1.0.1"]
                 [cor "0.1.0-SNAPSHOT"]]
  :jvm-opts ["-Xmx100m"])
