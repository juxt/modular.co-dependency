;; Copyright © 2015 Juan Antonio Ruz (juxt.pro)
(ns modular.component.co-dependency.schema
  (:require
   schema.utils schema.macros
   [schema.core :as s]
   [schema.spec.core :as spec]
   [schema.spec.collection :as collection]))

(defn deref? [x]
  (isa? (type x) clojure.lang.IDeref))

(defrecord Co-dependency [schema]
  s/Schema
  (spec [this]
    (collection/collection-spec
     (spec/simple-precondition this deref?)
     promise
     [(collection/one-element true schema (fn [item-fn coll] (item-fn @coll) nil))]
     (clojure.core/fn [_ xs _] (let [p (promise)] (deliver p (first xs))))))
  (explain [this] (list 'co-dep (s/explain schema))))

(defn co-dep [schema]
  (->Co-dependency schema))
