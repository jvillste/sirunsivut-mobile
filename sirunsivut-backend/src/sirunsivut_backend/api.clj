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
  (query {:select [:*] :from [:term_data]
          :where [:= :term_data.vid 2]}))

(defn ^:cor.api/stateles teasers [tid]
  (query {:select #_[:*] [:node_revisions.title :node_revisions.teaser :node.created]
          :from [:term_node]
          :left-join [:node [:= :node.nid :term_node.nid]
                      :node_revisions [:= :node_revisions.vid :node.vid]]
          :where [:and
                  (if tid
                    [:= :term_node.tid tid]
                    :true)
                  [:= :node.status 1]
                  [:= :node.promote 1]
                  #_[:like :node_revisions.title "Avoimuutta pak%"]]
          :order-by [[:node.created :desc]]}))

(defn nodes-for-term []
  {:select [:*] :from [:term_data]
   :where [:= :term_data.vid 2]})

#_(honeysql.core/format (terms))
#_(map :name (query (terms)))

#_(:status (first (teasers 86)))

#_(first (data/diff (first (teasers 26))
                    {:tid 26,
                     :format 2,
                     :moderate 0,
                     :vid 1042,
                     :title_2 "Avoimuutta pakoillaan yhtiöihin",
                     :uid 1,
                     :nid 1034,
                     :type "story",
                     :created 1481568804,
                     :nid_2 1034,
                     :title "Avoimuutta pakoillaan yhtiöihin",
                     :tnid 0,
                     :sticky 0,
                     :status 0,
                     :language "",
                     :comment 2,
                     :changed 1486205853,
                     :vid_2 1042,
                     :nid_3 1034,
                     :translate 0,
                     :uid_2 1,
                     :timestamp 1486205853,
                     :body
                     "<p>Julkisuuslain mukaan julkisen vallan asiakirjat ovat julkisia. Ruotsissa laki edellyttää samaa myös julkisomisteisilta yhtiöiltä. Suomessa ei laki tätä vielä vaadi. Katsotaan miten eduskunnassa käy. Miksi Vantaa ei voi olla tässä etunenässä?</p><p>Osakeyhtiölaki ei estä asiakirjojen julkisuutta. Esimerkiksi esityslistojen, pöytäkirjojen ja tilintarkastuskertomusten julkisuus soveltuvin osin, vaarantamatta kilpailua, vähentää korruptiota ja parantaa riskienhallintaa. Avoimuus ja läpinäkyvyys ovat toimivan demokratian edellytys.</p><p>Tuoreessa Rakennuslehden esittelemässä kirjassa ”Rakentamisen musta kirja – Rötösherroja ja kartellien solmijoita” (2016) summataan:</p><p>\"Tänään virkavastuuta pakoillaan yhtiöittämällä kunnan toimintoja ja salaamalla toiminta siltä julkisuudelta, mitä julkiselta toimijalta edellytetään.\"</p><p>Tässä kirjassa Vantaakin on valitettavan hyvin edustettuna.</p><p>- Kun veronmaksajien rahoilla läträtään ilman kontrollia, homma kusee aina johonkin suuntaan.</p><p>- Varoja käytetään holtittomasti, kilpailutus on huonoa ja on ulkomaanreissuja ja kadonneita kuitteja. Myös korruptio on helppo kätkeä. Tästä kaikesta on viime vuosina kokemusta Vantaalla.</p><p>Edellisen kerran, 2013, vastaava aloitteeni avoimuuden parantamiseksi kaupunkikonsernissa ei mennyt läpi. Aika ei ollut kypsä kahden suuren puolueen vastustaessa.&nbsp;</p><p>Julkisuuden puute lisää riskiä huonosta hallinnosta ja hyväveli-toiminnasta.</p><p>Valtuusto hyväksyi kieltävän vastauksen Antero Eerolan aloitteseen asiasta.</p><p>*</p><p><em>Puhe valtuustossa 12.12.2016</em></p><p><em>Avainsanat: Korrputio, avoimuus, hyväveli-verkosto.</em></p>",
                     :vid_3 1042,
                     :teaser
                     "<p>Julkisuuslain mukaan julkisen vallan asiakirjat ovat julkisia. Ruotsissa laki edellyttää samaa myös julkisomisteisilta yhtiöiltä. Suomessa ei laki tätä vielä vaadi.",
                     :log "",
                     :promote 1}))


