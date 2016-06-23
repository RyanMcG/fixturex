(defproject fixturex/fixturex "0.3.2-SNAPSHOT"
  :description "A library of helpful test fixture macros and functions."
  :url "http://www.ryanmcg.com/fixturex/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :repack [{:type :clojure
            :levels 1
            :path "src"}]
  :scm {:name "git"
        :url "https://github.com/RyanMcG/fixturex"}
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:dependencies [[incise "0.5.0"]
                                  [com.ryanmcg/incise-codox "0.1.0"]
                                  [com.ryanmcg/incise-vm-layout "0.5.0"]]
                   :plugins [[lein-repack "0.2.10"]]
                   :aliases {"incise" ^:pass-through-help ["run" "-m" "incise.core"]}}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0-alpha4"]]}})
