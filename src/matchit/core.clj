(ns matchit.core
  (:require [instaparse.core :as insta]))

(def exp "S = VALUE_EXP OPERATOR VALUE_EXP;
          OPERATOR =#\"(<|>)\";
          ATTR_ACCESSOR = #\"\\.\";
          CHAR=#\"[A-Za-z]\";
          NAME=CHAR+;
          ATTR_ACCESS = #\"\\.\",NAME;
          VALUE_EXP=NAME,ATTR_ACCESS")

(def filter-func
  (insta/parser exp))

(filter-func "abc.t>def.g")

;; (->> (filter-func "abc.t>def.g")
;;   (insta/transform
;;    {:> >, :< <
;;     :number clojure.edn/read-string :expr identity}))

