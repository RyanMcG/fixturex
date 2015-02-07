(ns fixturex.higher
  "This namespace contains a few macros for making it a bit easier to define
  common fixtures. Typically a fixture is meant to do setup, tear down, or wrap
  around to add some context. The before, after and around macros do these
  things.")

(defmacro befores-and-afters [before after]
  `(fn [f#]
     ~@before
     (f#)
     ~@after))

(defmacro before
  "Create a fixture which executes the body before the passed in fixture is
  invoked."
  [& body]
  `(befores-and-afters ~body nil))

(defmacro after
  "Create a fixture which executes the body after the passed in fixture is
  invoked."
  [& body]
  `(befores-and-afters nil ~body))

(defmacro before-and-after
  "Create a fixture with a single before and after expression."
  [before after]
  `(befores-and-afters (~before) (~after)))

(defmacro around
  "Create a fixture function which executes the body around the invoked
  fixture."
  [& body]
  `(fn [f#] (~@body (f#))))

(defmacro wrap
  "Like around but assumes the second argument is a vector (e.g. bindings)."
  [name & bindings]
  `(around ~name ~(vec bindings)))

(defmacro redefs
  "This is sugar for creating a fixture that redefines vars (via
  `with-redefs`).

    ; These are the same
    `(around with-redefs [bind ing])`
    `(redefs bind ing)`."
  [& bindings]
  `(wrap with-redefs ~@bindings))

(defmacro with
  "Create a fixture which sets the value of a var with the name of the given
  symbol to a delay on the given expression."
  [name & exprs]
  {:pre [(symbol? name)]}
  `(fn [f#]
     (declare ~name)
     (with-redefs [~name (delay ~@exprs)] (f#))))
