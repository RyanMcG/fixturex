(defproject fixturex/fixturex "0.2.1"
  :description "A library of helpful test fixture macros and functions."
  :url "http://www.ryanmcg.com/fixturex/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :repack [{:type :clojure
            :levels 1
            :path "src"}]
  :profiles {:dev {:dependencies [[incise-markdown-parser "0.2.0"]
                                  [incise-git-deployer "0.1.0"]
                                  [com.ryanmcg/incise-codox "0.1.0"]
                                  [incise-core "0.4.0"]
                                  [com.ryanmcg/incise-vm-layout "0.5.0"]]
                   :plugins [[lein-repack "0.2.4"]]
                   :aliases {"incise" ^:pass-through-help ["run" "-m" "incise.core"]}}})
