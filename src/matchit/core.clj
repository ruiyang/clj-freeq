(ns matchit.core
  (:require [instaparse.core :as insta]))

(def exp "EXP = (AND_EXPRESSION | GROUP_EXP) (<LOGICAL_OR> EXP)*
  <GROUP_EXP> = <SEPERATOR*> <\"(\"> <SEPERATOR*> EXP <SEPERATOR*> <\")\"> <SEPERATOR*>
  LOGICAL_AND = <SEPERATOR*> \"AND\" <SEPERATOR*>;
  LOGICAL_OR = <SEPERATOR*> \"OR\" <SEPERATOR*>;
  AND_EXPRESSION = (VALUE_EXP | FUNC_CALL) (<LOGICAL_AND> (VALUE_EXP | GROUP_EXP | FUNC_CALL))*
  FUNC_CALL = NAME <\"(\"> ((VALUE_EXP <FUNC_PARAMETER_SEPARATOR>)* VALUE_EXP |Epsilon) <\")\">
  <FUNC_PARAMETER_SEPARATOR> = \",\"
  <SEPERATOR> = \" \"+
  <OPERATOR> =#\"(<|>)\";
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
                    :EXP (fn [& args] (fn [data] (reduce #(or %1 (%2 data)) false args)))
                    })

(defn create-filter [filter-expression]
  (instaparse.core/transform
   transform-map
   ((insta/parser exp) filter-expression)))
