(ns fixturex.core-test
  (:require [clojure.test :refer :all]
            [fixturex.higher :refer :all]
            [fixturex.core :refer :all]))

(declare ^:dynamic *dvar*)
(defn bind-dvar-fixturer [v] (around binding [*dvar* v]))
(def bind-dvar-to-var (bind-dvar-fixturer :var))

(deftest test-with-fixtures-fn
  (with-fixtures-fn [bind-dvar-to-var]
                    (fn [] (is (= *dvar* :var)))))

(deftest test-with-fixtures
  (with-fixtures [bind-dvar-to-var]
    (is (= *dvar* :var))))

(deftest-fx test-deftest-fx [bind-dvar-to-var]
  (is (= *dvar* :var))
  (testing-fx "" [(bind-dvar-fixturer :testing)]
    (is (= *dvar* :testing))))

(deftest non-fixtured-test (is (= *dvar* :testing-again)))
(deftest test-testing-fx
  (is (thrown? ClassCastException (var-get *dvar*)))
  (testing-fx "non fixtured" [(bind-dvar-fixturer :testing-again)]
    (is (= *dvar* :testing-again))
    (non-fixtured-test)))

(defn test-ns-hook []
  (test-with-fixtures-fn)
  (test-with-fixtures)
  (test-deftest-fx)
  (test-testing-fx))
