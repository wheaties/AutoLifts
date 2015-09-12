---
layout: default
title: Lifters
category: Lifters
---
# Lifters

The first group of functionality that the AutoLifts library adds -- coincidentally the genesis of its name -- is a set of function wrappers and syntax extensions for type based automatic functional lifting and application. These components can be imported one of two ways, the first by importing all library functionality via the `AutoLift` object:

```tut:silent
import autolift._
import AutoLift._
```

and the second, by importing only the `Lifters` object:

```tut:silent
import autolift._
import Lifters._
```

## A Brief Introduction to Lifting

Lifting is a concept, a design pattern in functional languages, where given a type `A`, move it into the context of a higher-kinded type, `L[_]`, so that it may be acted upon within `L[_]`. In general, the type that is lifted is a function and the things acted upon already exist within the `L[_]` context. Without lifting the types would incompatible and much boilerplate would have to be written for each variant of `L[_]` and `A`. 

This is a fancy and indirect way of talking about how `Functors`, `Applicatives`, `Monads` and many other type classes are often incorporated into a code base. As a practical example, take a function of `A => B` and lift it into a `List[_]` context so that it takes on the form `List[A] => List[B]` by doing the following:

```tut:silent
def fmapList[A, B](f: A => B)(list: List[A]): List[B] = list.map(f)

val addOne = { x: Int => x+1 }
val lift = fmapList(addOne) _
```

For the curious, a more indepth discussion on the concept of lifting than found here can be had at the [Haskell wiki page](https://wiki.haskell.org/Lifting).

## Using Lifters

For cases where the application of a function is clear cut but the types dictate extra work on the developers part, the AutoLifts library starts to shine. Given the following function and nested types:

```tut:silent
val addOne = { x: Int => x+1 }
val value = Option(List(1, 2, 3))
val out = value map { list =>
  list map addOne
}
```

in order to make use of the function, two different maps had to be written. Using the lifting syntax extensions this could be rewritten

```tut:silent
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val value = Option(List(1, 2, 3))
val out = value liftMap { x: Int => x+1 }
```

or using the function wrappers it could be rewritten

```tut:silent
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val wrapped = liftMap{ x: Int => x+1 }
val out = wrapped(Option(List(1, 2, 3)))
```

