# fixturex [![Build Status](https://travis-ci.org/RyanMcG/fixturex.svg?branch=master)](https://travis-ci.org/RyanMcG/fixturex)

A library of helpful test fixture macros and functions for when namespace level
fixtures with `use-fixtures` is not specific enough.

```clojure
;; Add the following to dependencies in your project.clj
[fixturex "0.2.1"]
```

###### [**Source**](https://github.com/RyanMcG/fixturex)
###### [**API Documentation**][api]

## Usage

`fixturex` is meant to make using and defining fixtures in tests just a tiny bit easier.
It is a very small and simple library of tiny macros.

### `fixturex.higher`

Why am I not introducing the core namespace first?
`fixturex.higher` makes it a bit more concise to define fixtures which is necessary to show off the core part of this library.

```clojure
(use 'fixturex.higher)
(before (setup))                       ; → (fn [f#] (setup) (f#))
(after (tear-down))                    ; → (fn [f#] (f#) (tear-down))
(before-and-after (setup) (tear-down)) ; → (fn [f#] (setup) (f#) (tear-down))
(declare ^:dynamic *x*)
(around binding [*x* 1])               ; → (fn [f#] (binding [*x* 1] (f#)))
(with x 1)                             ; → (fn [f#]
                                       ;     (declare x)
                                       ;     (with-redefs [x (delay 1)] (f#)))
```

### `with-fixtures`

The `with-fixtures` macro can be use to execute some body of code with a sequence of fixtures.

```clojure
(require '[clojure.test :refer [is]])
(with-fixtures [(before (setup))
                (with conn (connect-to-db))
                (with x 1)]
  (do-something conn x)
  (with-fixtures [(with x 2)]
    (is (= @x 2)))
  (is (= @x 1)))
```

There is also a function version, `with-fixtures-fn`.

### `deftest-fx` &amp; `testing-fn`

*fixtures* defines `deftest-fx` and `testing-fn

```clojure
(defn connect-to-db [] ...)
(defn dissconnect-from-db [] ...)
(deftest-fx test-user-creation [(before-and-after (connect-to-db)
                                                  (dissconnect-from-db))]
  ...)
```

They use `with-fixtures` internally and are just a bit of sugar.

## `fixturex.context`

This namespace supports dynamic binding like in RSpec.
It accomplishes this by providing some functions which merge bindings onto a dynamic var (`*context*`) and by rewriting forms.
Some totally ridiculous examples follow.

```clojure
(require [fixturex.context :refer :all])
```

### `with-context`

This macro merges the given bindings onto the context and rewrites the body so symbols that match keys in the context are lookups.

```clojure
(with-context [:foo 1
               :bar (inc foo)]
  (assert (= bar 2)))
```

In RSpec this looks like:

```ruby
context do
  let(:foo) { 1 }
  let(:bar) { 2 }

  it { expect(bar).to eq(2) }
end
```

#### `testing-ctx`

This is just a shortcut for combining `clojure.test/testing` and `with-context`.

```clojure
(testing-ctx "description of context" [...bindings...]
  ...)

(testing "description of context"
  (with-context [...bindings...]
    ...))
```

#### `deftest-ctx`

This is just a shortcut for combining `clojure.test/deftest` and `with-context`.

```clojure
(deftest-ctx test-something [...bindings...]
  ...)

(deftest test-something
  (with-context [...bindings...]
    ...))
```

### `where`

Creates a fixture that adds bindings to the context.

```clojure
(require '[fixturex.core :refer [with-fixtures]])
(def a-fixture (where :foo 2))
(with-context [:foo 1]
  (assert (= foo 1))
  (with-fixtures [a-fixture]
    (assert (= foo 2))))
```

### `$`, `lookup`, and `scoped`

Functions which lookup in the context can be defined outside of where the context is defined.
There are several methods to lookup.

```clojure
(defn bar-is-one [] (scoped [bar] (assert (= bar 1))))
(defn foo-is-two [] (assert (= ($ foo) 2)))
(defn lol-is-three [] (assert (= (lookup :lol) 3)))

(with-context [:bar 1
               :foo 2
               :lol 3]
  (bar-is-one)
  (foo-is-two)
  (lol-is-three))
```

## A note on fixtures

*fixturex* uses the same mechanism for combining fixtures that `clojure.test` does.
This means that **you** *must be* **careful**.
It is easy to write a bad fixture and next to impossible to tell if a fixture does not do what the author intended.
Take the following example:

```clojure
(require '[clojure.test :refer [is]])
(require '[fixturex.core :refer [with-fixtures]])

(with-fixtures []
  (is (= 1 2))) ; → false
```
This prints:

    FAIL in ...
    expected: (= 1 2)
    actual: (not (= 1 2))

However, if we pass in a fixture which does not invoke its argument:

```clojure
(with-fixtures [(fn bad-fx [f] :derp)]
  (is (= 1 2))) ; → :derp
```

Nothing is printed.

If you have something like this in tests it may appear that certain tests are
passing when in fact they are not being invoked.

## License

Copyright © 2014 Ryan V McGowan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[api]: http://www.ryanmcg.com/fixturex/api/
