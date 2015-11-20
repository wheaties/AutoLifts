AutoLifts
=========

A dependently typed library for auto lifting and auto mapping of functions.

[![Build Status](https://secure.travis-ci.org/wheaties/AutoLifts.png)](http://travis-ci.org/wheaties/AutoLifts)

##Using

The current released branch is 0.3 and compiled against Scala version 2.11. If using SBT add the following:

```scala
libraryDependencies += "com.github.wheaties" %% "autolift" % "0.3"
```

Due note, the api will be subject to change as the library develops and progresses to a 1.0 release. As is, the library is in an experimental stage. Please use apropriately.

## Lifters
A generalization on the concept of lifting such that functions can be lifted to arbitrarily deep nestings and stacks of Functors, Applicatives, Monads and Traversables. Or, put another way, adds a more powerful `map`, `flatMap`, and `fold` that figures out where the best application site is based upon the type of the function.

```scala
scala> val addOne = { x: Int => x + 1 }

scala> val in = Option(List(1, 2, 3))
scala> in liftMap addOne
res0: Option[List[Int]] = Some(List(2, 3, 4))
```

Also included are generalized `liftMap` and `liftFlatMap` functions such that each wraps a function into an auto lifting context:

```scala
scala> val liftedOne = liftMap(addOne)
scala> liftedOne(List(1, 2, 3))
res1: List[Int] = List(2, 3, 4)

scala> liftedOne(List(Option(1), None))
res2: List[Option[Int]] = List(Some(2), None)
```

##Folders
A generalization on the concept of folding. Any traversable object with a defined instance of a `Foldable` yields folds over arbitrarily deep nestings.

```scala
scala> val nested = List(List("1", "2", "3"), Nil, List("4", "5", "6"))
scala> nested.foldWith{ x: String => x.toInt }
res4: Int = 21
```
