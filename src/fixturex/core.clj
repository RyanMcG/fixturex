(ns fixturex.core
  (:require [clojure.test :refer [join-fixtures deftest testing]]))

(defn- body-with-fixtures [fixtures body]
  `((join-fixtures ~fixtures) (fn [] ~@body)))

(defmacro deftest-fx [name fixtures & body]
  `(deftest ~name ~(body-with-fixtures fixtures body)))

(defmacro testing-fx [msg fixtures & body]
  `(testing ~msg ~(body-with-fixtures fixtures body)))
