---
layout: default
title: LiftFold
---

Like `LiftFoldLeft` and `LiftFoldRight`, `LiftFold` finds the first type contained within a nested set of types that has a `Monoid` defined and folds over that. For those that don't known what a `Monoid` is, [it is an algebraic structure with an associative binary operation that has an identity element](https://wiki.haskell.org/Monoid) Hence, `liftFold` is equivalent to `liftFoldLeft` but the arguments supplied via an implicit type class.

Unlike `LiftFoldLeft` and `LiftFoldRight`, there is no associated Context as there is no user supplied function which is being lifted. The operation is strictly determined by the first applicable type for which there is a `Monoid` and is foldable.

Contained: *Scalaz*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftFold` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val listOpt = Option(List(1,2,3))
val out = listOpt map { list: List[Int] =>
  list.foldLeft(0)(_ + _)
}
```

it is equivalent to

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val listOpt = Option(List(1,2,3))
val out = listOpt.liftFold
```