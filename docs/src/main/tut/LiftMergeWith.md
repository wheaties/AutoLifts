---
layout: default
title: LiftMergeWith
---

`LiftMergeWith` is an auto lifting operation related to `zipWith` which uses the type structure of the two objects and the function to determine the correct nesting level. It follows an `Applicative` sequencing of the function to produce the final result, i.e. given an `F[G[A]]`, `G[B]` and a `(A, B) => C)` produces a `F[G[C]]`. The concept is similar in nature to a left-hand side lifted `liftA2` such that a `G[_]` may be nested within an `F[_]`, an `F1[F2[_]]` all the way up to `F1[F2[..Fn[_]...]]` for some "n."

Contained: *Scalaz*, *Algebird*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftMerge` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val item1 = Option(List(1, 2))
val item2 = List(1, 2)
def add(x: Int, y: Int) = x + y

item1.map{ ints =>
  for{
    x <- itns
    y <- item2
  } yield add(x, y)
}
```

it could be rewritten

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val item1 = Option(List(1, 2))
val item2 = List(1, 2)
def add(x: Int, y: Int) = x + y

val out = item1.liftMerge(item2)(add)
```