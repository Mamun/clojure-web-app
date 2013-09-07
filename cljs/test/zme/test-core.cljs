(ns zme.test-core
(:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing)])
(:require [cemerick.cljs.test :as t]))


(deftest somewhat-less-wat
  (is (= "{}[]" (+ {} []))))

(deftest javascript-allows-div0
  (is (= js/Infinity (/ 1 0) (/ (int 1) (int 0)))))

(deftest test-data
  []
  (is (= 1 2)))
