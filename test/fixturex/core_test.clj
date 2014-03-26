(ns fixturex.core-test
  (:require [clojure.test :refer :all]
            [fixturex.higher :refer :all]
            [fixturex.core :refer :all]))

(declare ^:dynamic *dvar*)
(defn bind-dvar-to-fixturer [v] (around binding [*dvar* v]))

(deftest-fx test-foo1 [(bind-dvar-to-fixturer :var)]
  (is (= *dvar* :var))
  (testing-fx "" [(bind-dvar-to-fixturer :testing)]
    (is (= *dvar* :testing))))

(deftest non-fixtured-test (is (= *dvar* :testing-again)))
(deftest test-foo2
  (is (thrown? ClassCastException (var-get *dvar*)))
  (testing-fx "non fixtured" [(bind-dvar-to-fixturer :testing-again)]
    (is (= *dvar* :testing-again))
    (non-fixtured-test)))

(defn test-ns-hook []
  (test-foo1)
  (test-foo2))

(run-tests)
