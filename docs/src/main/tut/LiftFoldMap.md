---
layout: default
title: LiftFoldMap
---

`LiftFoldMap` is another variation on `LiftFold` similar in nature to `LiftMap`. Given a function of the form `A => B` where there exists a `Monoid` for `B`, it lifts the fold to the first applicable level such that `A => B` is transformed into `F[G[A]] => F[B]` where `G` is foldable. However, unlike a traditional lift which is restricted to a single fixed type, that constraint is removed.

Contained: *Scalaz*

## Syntax Extension

The syntax extension adds the method `liftFoldMap` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. One requirement of this method is that the return type must have a `Monoid`.

The following code fragment is equivalent

```tut
val listOpt = Option(List("1","2","3"))
val out = listOpt map { list: List[String] =>
  list.foldLeft(0)(_ + _.toInt)
}
```

to 

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val listOpt = Option(List("1","2","3"))
val out = listOpt.liftFoldMap{x: String => x.toInt}
```

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. The context acts similar to a function providing `andThen`, `compose` and `map` functions besides an `apply`. It should be noted that each pluggable back-end supplies its own variation of the context which works with that back-end's `Monoid` and will not be compatible with other back-ends.

To demonstrate:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val lifted = liftFoldMap{ x: Any => x.toString.size }
val single = lifted(List(1, 2, 3))
val doubly = lifted(List(Option(1), None))
```