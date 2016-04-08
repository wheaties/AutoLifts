---
layout: default
title: LiftMerge
---

`LiftMerge` is a tupling operation related to `zip` which uses the type structure of the two objects to determine the correct nesting level. It is equivalent to `Applicative` sequencing with the innate function producing a tuple. The concept behind this operation is that the `merge` of the second object is lifted into the first, i.e. `F[G[A]]` and `G[B]` will produce a `F[G[(A, B)]]`. Like the other auto-lifting operations in the library, the left hand type is allowed the form `F[A]`, `F1[F2[A]]` all the way up to `F1[F2[...Fn[A]...]]` for some value of `n`.

Contained: *Scalaz*, *Algebird*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftMerge` (in Algebird `liftJoin`) to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val item1 = Option(List(1, 2))
val item2 = List(1, 2)

val out = item1.map{ ints =>
  for{
    x <- ints
    y <- item2
  } yield (x, y)
}
```

it could be rewritten

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val item1 = Option(List(1, 2))
val item2 = List(1, 2)
val out = item1 liftMerge item2
```