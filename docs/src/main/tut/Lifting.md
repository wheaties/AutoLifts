---
layout: default
title: Lifting
---

## A Brief Introduction to Lifting

Lifting is a concept, a design pattern in functional languages, where given a type `A`, move it into the context of a higher-kinded type, `L[_]`, so that it may be acted upon within `L[_]`. For the purposes of this library, the type that is lifted is a function and the things acted upon already exist within the `L[_]` context. Without lifting the types would incompatible and much boilerplate would have to be written for each variant of `L[_]` and `A`. 

This is a fancy and indirect way of talking about how `Functors`, `Applicatives`, `Monads` and many other type classes are often incorporated into a code base. As a practical example, take a function of `A => B` and lift it into a `List[_]` context so that it takes on the form `List[A] => List[B]` by doing the following:

```tut:silent
def fmapList[A, B](f: A => B)(list: List[A]): List[B] = list.map(f)

val addOne = { x: Int => x+1 }
val lift = fmapList(addOne) _
```

For the curious, a more indepth discussion on the concept of lifting than found here can be had at the [Haskell wiki page](https://wiki.haskell.org/Lifting).

## Lifting in AutoLifts

For cases where the application of a function is clear cut but the types dictate extra work on the developers part, the AutoLifts library starts to shine. Given the following function and nested types:

```tut:silent
val addOne = { x: Int => x+1 }
val value = Option(List(1, 2, 3))
val out = value map { list =>
  list map addOne
}
```

in order to make use of the function, two different calls to `map` had to be written. Using the auto-lifting syntax extensions this could be rewritten

```tut:silent
import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._

val value = Option(List(1, 2, 3))
val out = value liftMap { x: Int => x+1 }
```

or using the contexts it could be rewritten

```tut:silent
import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._

val wrapped = liftMap{ x: Int => x+1 }
val out = wrapped(Option(List(1, 2, 3)))
```

