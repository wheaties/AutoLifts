---
layout: default
title: LiftFoldLeft
---

On an object which is foldable of type `F[A]`, `foldLeft` takes a function of the form `(B, A) => B` with a default value of `B` to produce a single value of `B`. The auto-lifting form of this would take any nested set of objects and "lift" the function with its default value to the first instance of an object which is both foldable and matches the signature of the function. Hence, it can work on an `F[A]`, an `G[F[A]]` and up to N nestings `G1[G2[...Gn[F[A]]...]]`.

Contained: *Scalaz*

## Syntax Extension

The syntax extension adds the method `liftFoldLeft` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val listOpt = Option(List(1,2,3))
val out = listOpt map { list: List[Int] =>
  list.foldLeft(0)(_ + _)
}
```

it could be rewritten

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val listOpt = Option(List(1,2,3))
val out = listOpt.liftFoldLeft(0){ (x: Int, y: Int) => x + y }
```

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type.

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val lifted = liftFoldLeft(0){ (x: Int, y: Int) => x + y }
val out1 = lifted(List(1, 2, 3))
val out2 = lifted(Option(List(1, 2, 3)))
```