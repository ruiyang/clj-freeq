(ns clj-freeq.core
  (:require [instaparse.core :as insta]
            [clojure.edn :as edn]))

(def exp "EXP = (AND_EXPRESSION | GROUP_EXP) (<LOGICAL_OR> EXP)*
  <GROUP_EXP> = <SEPERATOR*> <\"(\"> <SEPERATOR*> EXP <SEPERATOR*> <\")\"> <SEPERATOR*>
  LOGICAL_AND = <SEPERATOR*> \"AND\" <SEPERATOR*>;
  LOGICAL_OR = <SEPERATOR*> \"OR\" <SEPERATOR*>;
  AND_EXPRESSION = (VALUE_EXP | FUNC_CALL) (<LOGICAL_AND> (VALUE_EXP | GROUP_EXP | FUNC_CALL))*
  FUNC_CALL = NAME <\"(\"> <SEPERATOR*> (((VALUE_EXP | LITERAL) <FUNC_PARAMETER_SEPARATOR>)* (VALUE_EXP | LITERAL) |Epsilon) <SEPERATOR*>  <\")\">
  <LITERAL> = (STRING | DIGIT)
  STRING = <'\"'> #'[^\"]*' <'\"'>
  DIGIT = ('-')? #\"[0-9]+\"
  <FUNC_PARAMETER_SEPARATOR> = SEPERATOR? \",\" SEPERATOR?
  <SEPERATOR> = \" \"+
  NAME=#\"[A-Za-z]+\";
  VALUE_EXP=<SEPERATOR*> NAME(<#\"\\.\">NAME)* <SEPERATOR*>")

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
      {key (transform-to-map (nth (rest vec) 0))}
      {key (rest vec)})))

(defn parse [filter-expression]
  "given an expression, return the AST"
  (transform-to-map
   (insta/transform
    {:VALUE_EXP (fn [& args] (clojure.string/join "." args))
     :NAME (fn [& args] (apply str args))}
    ((insta/parser exp) filter-expression))))
