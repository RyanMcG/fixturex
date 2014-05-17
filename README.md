# fixturex [![Build Status](https://travis-ci.org/RyanMcG/fixturex.svg?branch=master)](https://travis-ci.org/RyanMcG/fixturex)

A library of helpful test fixture macros and functions for when namespace level
fixtures with `use-fixtures` is not specific enough.

```clojure
;; Add the following to dependencies in your project.clj
[fixturex "0.1.0"]
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
                                       ;     (with-redefs [x 1] (f#)))
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
    (is (= x 2)))
  (is (= x 1)))
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

## License

Copyright © 2014 Ryan V McGowan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[api]: http://www.ryanmcg.com/fixturex/api/
