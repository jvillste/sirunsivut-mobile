#!/bin/bash lein exec

(require '[clojure.java.shell :as shell])

(defn current-date []
  (let [calendar (java.util.GregorianCalendar.)]
    {:year (.get calendar java.util.Calendar/YEAR)
     :month  (+ 1 (.get calendar java.util.Calendar/MONTH))
     :day (.get calendar java.util.Calendar/DAY_OF_MONTH)}))

(defn run-remote [& commands]
  (apply shell/sh "ssh" "root@95.85.52.76" commands))

#_(defn copy-file [file-name target-folder]
    (shell/sh "scp" (str "root@95.85.52.76:" file-name) file-name)
    (shell/sh "mv" file-name (str target-folder "/")))

(def compiled-directory "sirunsivut/sites/all/themes/siru/javascript/compiled")

(defn zip-mobile-app []
  (shell/sh "cd" compiled-directory)
  (shell/sh "zip" "-r" "mobile.zip" "."))

(zip-mobile-app)

#_(let [{:keys [year month day]} (current-date)
        date-string (str year "_" month "_" day)
        sql-file-name (str "sirunsivut_" date-string ".sql.gz")
        html-file-name (str "sirunsivut_html_" date-string ".zip")
        backup-folder "/Users/jukka/Google Drive/jukka/sirun_sivut"
        backup-file (partial backup-file backup-folder)]

    (backup-file sql-file-name
                 (str "mysqldump -uroot -pTombspss2 sirunsivut | gzip > " sql-file-name))

    (backup-file html-file-name
                 (str "cd /var/www/html; zip -r /root/" html-file-name " ."))
    
    (println "ready."))
