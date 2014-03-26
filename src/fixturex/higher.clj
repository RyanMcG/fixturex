(ns fixturex.higher)

(defmacro around [& body] `(fn [f#] (~@body (f#))))
(defmacro ^:private before-and-after-core [before after]
  `(fn [f#]
     ~@before
     (f#)
     ~@after))

(defmacro before [& body] `(before-and-after-core ~body nil))
(defmacro after [& body] `(before-and-after-core nil ~body))

(defmacro before-and-after
  [before after]
  `(before-and-after-core (~before) (~after)))

; (before (println "ABC"))
; (after (println "ZXY") :hey)
; (before-and-after (println "Derp") (println "HEY"))
; (around println)
