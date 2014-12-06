(ns fixturex.context-test
  (:require [clojure.test :refer :all]
            [fixturex.context :refer :all]
            [fixturex.core :refer [testing-fx]]))

(deftest test-replace-bound-vars-in-body
  (letfn [(replaces-body [bound-syms in-body out-body]
            (is (= (replace-bound-vars-in-body bound-syms in-body)
                   out-body)))]
    (testing "top-level form"
      (replaces-body #{:blahg}
                     `(+ 1 blahg)
                     `(+ 1 (lookup :blahg))))
    (testing "nested forms"
      (replaces-body #{:check :boom}
                     `((+ 1 boom)
                       (check))
                     `((+ 1 (lookup :boom))
                       ((lookup :check)))))
    (testing "prexisting lookup"
      (replaces-body #{:batman}
                     `(lookup :batman)
                     `(lookup :batman)))))

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

(deftest-ctx test-ctx-suffixed [:ryan 7
                                :pam 6]
  (is (= 6 pam))
  (is (= 7 ryan))
  (testing-ctx "hotter ryan" [:ryan 8]
    (is (= 6 pam))
    (is (= 8 ryan))
    (testing-fx "equally hot pam" [:ryan 9
                                   :pam ryan]
      (is (= ryan pam 9)))))

(deftest-ctx test-lookup [:hey 1]
  (testing "sanity" (is (= hey 1)))
  (testing "valid lookup"
    (is (= (lookup :hey) 1)))
  (testing "invalid lookup"
    (is (thrown-with-msg? AssertionError
                          #"not-a-valid-key does not exist in context"
                          (lookup :not-a-valid-key)))))
