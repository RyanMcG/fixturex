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
