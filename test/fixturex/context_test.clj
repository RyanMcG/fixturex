(ns fixturex.context-test
  (:require [clojure.test :refer :all]
            [fixturex.context :refer :all]
            [fixturex.core :refer [testing-fx]]))

(defn bar-is-ten [] (scoped [bar] (is (= bar 10))))
(defn lol-is-111 [] (is (= ($ lol) 642)))

(deftest test-with-context
  (with-context [:foo 1
                 :bar (* 5 foo)]
    (is (= foo 1))
    (is (= bar 5))
    (testing-fx "with a where" [(where :foo 2
                                       :lol (+ foo bar 630))]
      (is (= foo 2))
      (bar-is-ten)
      (lol-is-111))
    (is (= foo 1))
    (is (= bar 5))))
