(ns matchit.core
  (:require [instaparse.core :as insta]))

(def data {:abc {:def true} :def {:gh false} :hh {:ii true} :gg {:ff true}})

(def exp "EXP = (AND_EXPRESSION | GROUP_EXP) (<LOGICAL_OR> EXP)*
  <GROUP_EXP> = <SEPERATOR*> <\"(\"> <SEPERATOR*> EXP <SEPERATOR*> <\")\"> <SEPERATOR*>
  LOGICAL_AND = <SEPERATOR*> \"AND\" <SEPERATOR*>;
  LOGICAL_OR = <SEPERATOR*> \"OR\" <SEPERATOR*>;
  AND_EXPRESSION = VALUE_EXP (<LOGICAL_AND> (VALUE_EXP | GROUP_EXP))*
  <SEPERATOR> = \" \"+
  <OPERATOR> =#\"(<|>)\";
  NAME=#\"[A-Za-z]\"+;
  VALUE_EXP=<SEPERATOR*> NAME(<#\"\\.\">NAME)* <SEPERATOR*>")

(def transform-map {:ATTR_ACCESS (fn [& args] (keyword (nth args 1)))
                    :NAME (fn [& args] (apply str args))
                    :VALUE_EXP (fn [& args] (fn [data] (get-in data (map keyword args))))
                    :AND_EXPRESSION (fn [& args] (fn [data] (reduce #(and %1 (%2 data)) true args)))
                    :OPERATOR (fn [& args] (fn [x y] (> x y)))
                    :EXP (fn [& args] (fn [data] (reduce #(or %1 (%2 data)) false args)))
                    })

((instaparse.core/transform
  transform-map
  ((insta/parser exp) "abc.def AND (def.gh AND def.gg OR hh.ii)")) data)

(defn create-filter [filter-expression]
  (instaparse.core/transform
   transform-map
   ((insta/parser exp) filter-expression)))

(def test-filter (create-filter "abc.def AND (def.gh AND def.gg OR hh.ii)"))

(test-filter data)
