---
layout: default
title: LiftForAll
---

`LiftForAll` is a more general `forall` with the ability to auto-deduce where a given predicate should be applied based upon the argument type of the predicate and the applicability of the type structure to which it is applied. That is, `liftForAll` can lift a predicate into the first suitable context for which it can be applied. Hence, given a predicate of the form `A => Boolean` it can be applied to `F[A]`, `F1[F2[A]]` and all the way to `F1[F2[...Fn[A]...]]` for any `n`.

Underneath the hood, `LiftForAll` operates by calling successive `map` operations until it finds the first type for which `A => Boolean` may act. If the argument type, `A`, is a general type such as `Any`, it will be applied to the first correct type such `F[B]` that `A >: B`. There is currently no forcing mechanism to coerce the application point lower in the type structure.

Contains: *Scalaz*

## Syntax Extension

The syntax extension adds the method `liftForAll` (`liftAll` in the case of Scalaz) to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val optList = Option(List(2, 4, 6, 8))
val out = optList.map{ 
  _.forall(_ % 3 == 0)
}
```

the same can be had with

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val optList = Option(List(2, 4, 6, 8))
val out = optList.liftAll{ x: Int => x % 3 == 0 }
```

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. 

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val lifted = liftAll{ x: Int => x.toString.size < 2 }
val single = lifted(List(1, 10, 100))
val doubly = lifted(NonEmptyList(List(1, 10, 100)))
```