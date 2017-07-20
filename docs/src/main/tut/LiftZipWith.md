---
layout: default
title: LiftZipWith
---

`LiftZipWith` is a more powerful `zipWith` which uses the type structure of the two objects plus the function to determine the correct nesting level. The concept behind this operation is that the `zipWith` of the second object is lifted into the first, i.e. `F[G[A]]`, `G[B]` and `(A, B) => C` will produce an `F[G[C]]`. Like the other auto-lifting operations in the library, the left hand type is allowed the form `F[A]`, `F1[F2[A]]` all the way up to `F1[F2[...Fn[A]...]]` for some value of `n`.

Contained: *Scalaz*

## Syntax Extension

The syntax extension adds the method `liftZipWith` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val item1 = Option(List(1, 2))
val item2 = List(1, 2)

val out = item1.map{ inner: List[Int] =>
  (inner zip item2) map { case (x, y) => x+y }
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

val out = item1.liftZipWith(item2)(add)
```