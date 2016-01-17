---
layout: default
title: FoldExists
---

`FoldExists` is a more powerful `exists` which uses the type structure of the object and predicate to automatically determine correct nested level at which to begin folding. The concept behind the implementation is analogous to a traditional `fold` but recursively across the type structure. Hence, given a predicate of the form `A => Boolean` it is free to act upon a structure of `F[A]`, `F1[F2[A]]` and all the way up to `F1[F2[...Fn[A]...]]` to produce a `Boolean`.

Contains: *Scalaz*, *Cats*

## Syntax Extension

The syntax extension adds the method `foldExists` (`foldAny` for Scalaz) to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldLeft(false){
  _ || _.exists(_ > 3)
}
```

using AutoLifts it could be written in a single line:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldAny{ x: Int => x > 3 }
```

## Context

The context allows the predicate to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val folded = foldAny{ x: String => x.length < 2 }
val resTrue = folded(List("1", "2", "3"))
val resFalse = folded(List(Set("1"), Set("2", "32")))
```