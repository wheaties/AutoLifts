---
layout: default
title: LiftFoldAt
---

Another variant of `LiftFold`, `LiftFoldAt` exists to circumvent the type eager nature so that underlying types which themselves are foldable and have a `Monoid` can be folded, i.e. it allows a fold deeper in the type stack. To wit, if there exists a type `F[G[A]]` such that `F` is foldable, `G[A]` has a `Monoid` for any `A` and is foldable, and there exists an `Monoid` for `A`, `LiftFold` would operate on `F` over `G[A]` while `LiftFoldAt` would have the option lift the fold onto `G` over `A`.

Unlike `LiftFoldLeft` and `LiftFoldRight` but like `LiftFold`, there is no associated Context as there is no user supplied function which is being lifted.

Contained: *Scalaz*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftFoldAt` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. One requirement of this method is that the chosen higher-kinded type paramter contain directly within it a type which has a `Monoid`.

As an example:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

val futListOpt = Future(List(Option(1), None, Option(2)))
val out = futListOpt.liftFoldAt[Option]
```

which will force the evaluation at the `Int`, bypassing both the `List`, which itself is a `Monoid` for any type and the `Option` which by way of a recurance relation is a `Monoid` for any type.