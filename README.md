# juxt.modular/co-dependency

## Releases and Dependency Information

```clojure
[juxt.modular/co-dependency "0.2.0"]
```

## Usage

#### Add co-dependency to your project dependencies

```clojure

(defproject your-project "your-version"
   ...
   :dependencies [[juxt.modular/co-dependency "0.2.0"]]
   ...
   )

```


#### Add component and co-dependency to your ns:

```clojure

(ns your-app-ns
  (:require [com.stuartsierra.component :refer (using)]
            [modular.component.co-dependency :refer (co-using)]))


```

#### Define your system

[Same as you do](https://github.com/stuartsierra/component/blob/master/test/com/stuartsierra/component_test.clj#L114-L121) with stuartsierra/component lib but adding co-dependencies with co-dependency/co-using fn
**In this case :b depends on :a and :a co-depends on :b**

```clojure
(defn system-1 []
  (map->System1 {:a (-> (component-a)
                        (co-using [:b]))
                 :b (-> (component-b)
                        (using [:a]))
                 :c (-> (component-c)
                        (using [:a :b]))
                 :d (-> (component-d)
                        (using {:b :b :c :c}))
                 :e (component-e)})

```

#### Start your system
```clojure
(def system-started-with-co-deps (co-dependency/start-system (system-1)))
```

#### Retrieving co-dependencies values
```clojure
(def a (-> system-started-with-co-deps :a))
(def a-from-b (:a @(-> system-started-with-co-deps :a :b)))
;; checking identity equality
(assert (= a a-from-b))
```

#### Using stuartsierra reloaded workflow

If you use stuartsierra ["reloaded" workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) then update original stuartsierra dev/start function by:
```clojure
(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system co-dependency/start-system))
```

#### Do you need more help?
Follow the test provided to learn how to use it :)


## Drawbacks
In contrast with normal dependencies that you get using clojure map functions

```clojure
(:dependency-key component)
;;=> dependency
```

when you want to retrieve a co-dependency you need to deref the co-dependency value

```clojure
@(:co-dependency-key component)
;;=> co-dependency
```

### Co-dependencies are NOT bound during a component's start phase.

Co-dependencies are bound _after_ a component's start phase. Therefore,
a component's co-dependencies will not be accessible, even via a
dereference of the co-dependency, during a component's start.

Re-read that last paragraph until you understand that you _cannot_ make
use of a co-dependency's data until after the `start` phase of a given
component. You _can_ make use of a co-dependency's data _after_ the
component has been fully started, which usually means in a function
(such as a Ring handler).

The penalty for not understanding this point is days spent figuring out
why you can't see a co-dependency's data. It's because you're still
trying to access this data within the `start` phase of your
component. Don't do this. Defer the lookup until after the entire start
phase of your system has completed.

## License

Copyright © 2014 Juan Antonio Ruz (juxt.pro)

Distributed under the Eclipse Public License, the same as Clojure
