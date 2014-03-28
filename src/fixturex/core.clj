(ns fixturex.core
  "Some helpful macros for using fixtures. These are particularly helpful when
  defining tests. Often times a fixture should only be used for a subset of
  tests in a namespace or even just a couple of contexts in a few tests.
  clojure.text/use-fixtures does not operate at this level of granularity."
  (:require [clojure.test :refer [join-fixtures deftest testing]]))

(defn with-fixtures-fn
  "Join the given fixtures and invoke the given function with them."
  [fixtures f]
  ((join-fixtures fixtures) f))

(defmacro with-fixtures
  "Evaluate the body with the given fixtures."
  [fixtures & body]
  `(with-fixtures-fn ~fixtures (fn [] ~@body)))

(defmacro deftest-fx
  "Defines a test just like clojure test, but with the given fixtures around the
  body."
  [name fixtures & body]
  `(deftest ~name (with-fixtures ~fixtures ~@body)))

(defmacro testing-fx
  "Define testing context just like testing but with the given fixtures around
  the body."
  [msg fixtures & body]
  `(testing ~msg (with-fixtures ~fixtures ~@body)))
