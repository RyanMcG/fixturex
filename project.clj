(defproject fixturex "0.1.0-SNAPSHOT"
  :description "A library of helpful test fixture macros and functions."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[com.ryanmcg/incise-codox "0.1.0"]]
                   :aliases {"incise" ^:pass-through-help ["run" "-m" "incise.core"]}}})
