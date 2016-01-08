---
layout: default
title: LiftFoldRight
---

On an object which is foldable of type `F[A]`, `foldRight` takes a function of the form `(A, B) => B` with a default value of `B` to produce a single value of `B`. The auto-lifting form of this would take any nested set of objects and "lift" the function with its default value to the first instance of an object which is both foldable and matches the signature of the function. Hence, it can work on an `F[A]`, an `G[F[A]]` and up to N nestings `G1[G2[...Gn[F[A]]...]]`.

For the implementation backed by Scalaz, the function must have the form `(A, => B) => B` since it applies the operations in a lazy manner.

Contained: *Scalaz*

## Syntax Extension

The syntax extension adds the method `liftFoldRight` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val listOpt = Option(List(1,2,3))
val out = listOpt map { list: List[Int] =>
  list.foldRight(0)(_ + _)
}
```

it could be rewritten

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

def lazyAdd(x: Int, y: => Int) = x + y

val listOpt = Option(List(1,2,3))
val out = listOpt.liftFoldRight(0)(lazyAdd)
```

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type.

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

def lazyAdd(x: Int, y: => Int) = x + y

val lifted = liftFoldRight(0)(lazyAdd)
val out1 = lifted(List(1, 2, 3))
val out2 = lifted(Option(List(1, 2, 3)))
```