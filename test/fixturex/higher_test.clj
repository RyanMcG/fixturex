(ns fixturex.higher-test
  (:require [clojure.test :refer :all]
            (fixturex [core :refer [with-fixtures]]
                      [higher :refer :all])))

(deftest test-before
  (let [counter (atom 0)]
    (with-fixtures [(before (swap! counter inc))]
      (is (= @counter 1)))
    (is (= @counter 1))))

(deftest test-after
  (let [counter (atom 1)]
    (with-fixtures [(after (swap! counter dec))]
      (is (= @counter 1)))
    (is (= @counter 0))))

(deftest test-before-and-after
  (let [counter (atom 0)]
    (with-fixtures [(before-and-after (swap! counter inc) (swap! counter dec))]
      (is (= @counter 1)))
    (is (= @counter 0))))

(deftest test-befores-and-afters
  (let [counter (atom 0)]
    (with-fixtures [(befores-and-afters
                      ((swap! counter inc) (swap! counter inc))
                      ((swap! counter dec) (swap! counter dec)))]
      (is (= @counter 2)))
    (is (= @counter 0))))

(declare ^:dynamic *awesome*)
(deftest test-around
  (with-fixtures [(around binding [*awesome* :yeah!])]
    (is (bound? #'*awesome*))
    (is (= *awesome* :yeah!)))
  (is (not (bound? #'*awesome*))))

(deftest test-with
  (with-fixtures [(with y 3)]
    (is (= @y 3))
    (with-fixtures [(with y identity)]
      (is (not= @y 3))
      (is (= @y identity)))))
