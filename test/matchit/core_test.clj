(ns matchit.core-test
  (:require [clojure.test :refer :all]
            [matchit.core :refer :all]))

(defn test-expression-with-data [expression data]
  "Given an expression, create the according filter function and apply it to data."
  ((create-filter expression) data))

(def sample-data {
           :name "simple name"
           :age 25
           :gender "male"
           :single true
           :vegeterian false
           :address {
                     :city "Melbourne"
                     :street "david street"
                     :post-code 1234
                     }})

(deftest single-value-expression-access-test
  (testing "should evaluate single value access expression"
    (is
     (= (test-expression-with-data "abc" {:abc {:def 123}})
        {:def 123})))
  (testing "should evaluate single value access expression"
    (is
     (= (test-expression-with-data "address.city" sample-data)
        "Melbourne")))
  )

(deftest simple-and-expression
  (testing "should evaluate simple and expression"
    (is
     (= (test-expression-with-data "vegeterian AND single" sample-data)
        false))))

(deftest simple-function-call
  (testing "should call simple function"
    (are [expression expected-result] (= (test-expression-with-data expression sample-data) expected-result)
         "equal(name,\"simple name\")" true
         "equal(age,24)" false
         "equal(age, 24)" false
         "equal(age, 24 )" false
         "equal(age,25)" true
         "equal(age, 25 )" true)))
;; (run-all-tests #"matchit.core-test")
