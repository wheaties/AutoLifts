AutoLifts
=========

A dependently typed library for auto lifting and auto mapping of functions based on category theory but exposing an api that works in harmony with the standard Scala library.

[![Build Status](https://secure.travis-ci.org/wheaties/AutoLifts.png)](http://travis-ci.org/wheaties/AutoLifts) [![Join the chat at https://gitter.im/wheaties/AutoLifts](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/wheaties/AutoLifts?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Modules
AutoLifts is organized in a multi-project structure. The following modules are available for download: 

 * [`autolift-core`](https://github.com/wheaties/AutoLifts/tree/0.6/autolift-core/) - contains type class definitions, syntax extensions and contexts.
 * [`autolift-algebird`](https://github.com/wheaties/AutoLifts/tree/0.6/autolift-algebird) - an implementation of core type classes backed by the [Algebird](https://github.com/twitter/algebird) project.
 * [`autolift-cats`](https://github.com/wheaties/AutoLifts/tree/0.6/autolift-cats) - an implementation of core type classes backed by the [Cats](https://github.com/non/cats/) project.
 * [`autolift-scalaz`](https://github.com/wheaties/AutoLifts/tree/0.6/autolift-scalaz) - an implementation of core type classes backed by the [Scalaz](https://github.com/scalaz/scalaz/) project.

The *core* module is a dependency of *algebird*, *cats* and *scalaz*. While it forms the backbone of AutoLifts and can be downloaded, it is not a standalone project. Subprojects such as [docs](https://github.com/wheaties/AutoLifts/tree/0.5/docs) and [bench](https://github.com/wheaties/AutoLifts/tree/0.5/bench) are for reference only.

##Using
The current released branch is 0.5 and compiled against Scala version 2.11. If using SBT add the following to the build definition:

```scala
libraryDependencies += "com.github.wheaties" %% "autolift-[backend]" % "0.5"
```

where "backend" is one of *algebird*, *cats* or *scalaz*. As stated above, *core* will be downloaded as a dependency.

Due note, the api will be subject to change as the library develops and progresses to a 1.0 release. We do not, however, expect there to be any fundamental changes to functionality. Also of note, we expect that the majority of common use items will remain a permanent fixture from this point forward, i.e. `liftMap`, `liftFlatMap`, etc.

## Lifting
A generalization on the concept of lifting such that functions can be lifted to arbitrarily deep nestings and stacks of Functors, Applicatives, Monads and Traversables. Or, put another way, adds a more powerful `map`, `flatMap`, and `fold` that figures out where the best application site is based upon the type of the function. This functionality is exposed via syntax extensions and auto-lifting contexts.

For example, working with a nested type structure, syntax extensions make the following possible:

```scala
scala> def addOne(x: Int) = x + 1

scala> val in = Option(List(1, 2, 3))
scala> in liftMap addOne
res0: Option[List[Int]] = Some(List(2, 3, 4))
```

or alternatively the same could be accomplished by wrapping a function in a reusable context:

```scala
scala> val liftedOne = liftMap(addOne)
scala> liftedOne(List(1, 2, 3))
res1: List[Int] = List(2, 3, 4)

scala> liftedOne(List(Option(1), None))
res2: List[Option[Int]] = List(Some(2), None)
```

##Folding
A generalization on the concept of folding. Any traversable object yields folds over arbitrarily deep nestings. See documentation for a more indepth understanding:

```scala
scala> val nested = List(List("1", "2", "3"), Nil, List("4", "5", "6"))
scala> nested.foldWith{ x: String => x.toInt }
res4: Int = 21
```
