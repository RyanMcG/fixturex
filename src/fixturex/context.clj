(ns fixturex.context
  (:require [fixturex.higher :refer [around]]
            [clojure.walk :refer [postwalk]]))

(declare ^:dynamic *context*)

(defn lookup [k] ((*context* (keyword k))))
(defn $* [name] `(lookup ~(keyword name)))
(defmacro $ [name] ($* name))

(defn replace-bound-vars-in-body
  "Replace appearances of symbols bound in the current context with lookup
  calls in the given body."
  [bound-symbols body]
  (postwalk
    (fn replace-bound-vars-in-from [form]
      (if (symbol? form)
        (if (bound-symbols form)
          ($* form)
          form)
        form))
    body))

(defn symbol-set [maybe-syms] (set (map (comp symbol name) maybe-syms)))

(defmacro scoped [sym-names body]
  (replace-bound-vars-in-body (symbol-set sym-names) body))

(defn binding-pairs->context
  "Create a context map from a sequence of keyword form pairs.

  A context map has keyword keys and the values are forms of thunks."
  [bound-symbols binding-pairs]
  {:pre [(every? (comp keyword? first) binding-pairs)]}
  (->> binding-pairs
       (map (fn [[k v]]
              [(keyword k)
               (list 'fn []
                     (replace-bound-vars-in-body bound-symbols v))]))
       (into {})))

(defn- pairs->bound-symbols [binding-pairs]
  {:post [(set? %)
          (every? symbol? %)]}
  (symbol-set (map first binding-pairs)))

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
