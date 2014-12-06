(ns fixturex.context
  "Various functions for easy dynamic context (e.g. RSpec)."
  (:require [fixturex.higher :refer [around]]
            [clojure.test :refer [testing deftest]]
            [clojure.walk :refer [postwalk]]))

(declare ^:dynamic *context*)

(defn lookup
  "Lookup the thunk at the given key in the context and invoke it if it exists"
  [k]
  (let [thunk (*context* k)]
    (assert thunk (str (name k) " does not exist in context"))
    (thunk)))

(def ^:private nkw (comp keyword name))

(defn $* [form] `(lookup ~(nkw form)))
(defmacro $
  "Lookup the given Named in context."
  [form] ($* form))

(defn replace-bound-vars-in-body
  "Replace appearances of symbols bound in the current context with lookup
  calls in the given body."
  [bound-symbols body]
  (postwalk
    (fn replace-bound-vars-in-from [form]
      (cond
        (symbol? form) (if (contains? bound-symbols (nkw form))
                         ($* form)
                         form)
        :else form))
    body))

(def ^:private bound-names-set (comp set (partial map nkw)))

(defmacro scoped
  "Replace given bindings with context lookups."
  [sym-names & body]
  `(do ~@(replace-bound-vars-in-body (bound-names-set sym-names) body)))

(defn binding-pairs->context
  "Create a context map from a sequence of keyword form pairs.

  A context map has keyword keys and the values are forms of thunks."
  [bound-symbols binding-pairs]
  {:pre [(every? (comp keyword? first) binding-pairs)]}
  (into {}
        (for [[k v] binding-pairs]
          [k (list 'fn [] (replace-bound-vars-in-body bound-symbols v))])))

(defn- pairs->bound-symbols [binding-pairs]
  {:pre [(sequential? binding-pairs)
         (every? sequential? binding-pairs)]
   :post [(set? %)
          (every? keyword? %)]}
  (bound-names-set (map first binding-pairs)))

(defmacro with-context
  "Define context bindings around the given body."
  [bindings & forms]
  {:pre [(vector? bindings)
         (even? (count bindings))
         (every? keyword? (map first (partition 2 bindings)))]}
  (let [binding-pairs (partition 2 bindings)
        bound-symbols (pairs->bound-symbols binding-pairs)
        context (binding-pairs->context bound-symbols binding-pairs)
        body (replace-bound-vars-in-body bound-symbols forms)]
    `(binding [*context* (merge (if (bound? #'*context*)
                                  *context*
                                  {})
                                ~context)]
       ~@body)))

(defmacro where
  "Create a fixture for adding or modifying bindings in context."
  [& bindings]
  `(around with-context ~(vec bindings)))

(defmacro deftest-ctx
  "Defines a test just like clojure test, but with the given bindings context
  around the body."
  [name bindings & body]
  `(deftest ~name (with-context ~bindings ~@body)))

(defmacro testing-ctx
  "Define testing context just like testing but with the given context bindings
  around the body."
  [msg bindings & body]
  `(testing ~msg (with-context ~bindings ~@body)))
