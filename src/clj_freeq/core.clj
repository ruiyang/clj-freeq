(ns clj-freeq.core
  (:require [instaparse.core :as insta]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def exp (slurp (io/file
                 (io/resource "grammer.txt"))))

(def customize-function-map {
                             :equal =
                             })

(def transform-map {:ATTR_ACCESS (fn [& args] (keyword (nth args 1)))
                    :NAME (fn [& args] (apply str args))
                    :VALUE_EXP (fn [& args] (fn [data] (get-in data (map keyword args))))
                    :AND_EXPRESSION (fn [& args] (fn [data] (reduce #(and %1 (%2 data)) true args)))
                    :FUNC_CALL (fn [& args] (fn [data] (apply ((keyword (nth args 0)) customize-function-map) (map #(% data) (rest args)))))
                    :STRING (fn [& args] (fn [data] (nth args 0)))
                    :DIGIT (fn [& args] (fn [data] (edn/read-string (nth args 0))))
                    :EXP (fn [& args] (fn [data] (reduce #(or %1 (%2 data)) false args)))
                    })

(defn create-filter [filter-expression]
  (instaparse.core/transform
   transform-map
   ((insta/parser exp) filter-expression)))

(defn- transform-to-map [vec]
  (let [key (get vec 0)
        val (get vec 1)]
    (if (vector? val)
      {key (map transform-to-map (rest vec))}
      {key (rest vec)})))

(defn parse [filter-expression]
  "given an expression, return the AST"
  (transform-to-map
   (insta/transform
    {:VALUE_EXP (fn [& args] (clojure.string/join "." args))
     :NAME (fn [& args] (apply str args))
     :STRING (fn [& args] (str \' (nth args 0) \'))
     :DIGIT (fn [& args] (str (nth args 0)))
     :AND_EXPRESSION (fn [& args] (if (< (count args) 2) (nth args 0) (let [new-args [:AND]]
                                                                        (into new-args args))))
     :EXP (fn [& args] (if (< (count args) 2) (nth args 0) (let [new-args [:OR]]
                                                             (into new-args args))))
     }
    ((insta/parser exp) filter-expression))))
