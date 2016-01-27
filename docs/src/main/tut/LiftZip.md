---
layout: default
title: LiftZip
---

`LiftZip` is a more powerful `zip` which uses the type structure of the two objects to determine the correct nesting level. The concept behind this operation is that the `zip` of the second object is lifted into the first, i.e. `F[G[A]]` and `G[B]` will produce a `F[G[(A, B)]]`. Like the other auto-lifting operations in the library, the left hand type is allowed the form `F[A]`, `F1[F2[A]]` all the way up to `F1[F2[...Fn[A]...]]` for some value of `n`.

Contained: *Scalaz*

## Syntax Extension

The syntax extension adds the method `liftZip` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val item1 = Option(List(1, 2))
val item2 = List(1, 2)

val out = item1.map{ 
  _ zip item2
}
```

it could be rewritten

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val item1 = Option(List(1, 2))
val item2 = List(1, 2)
val out = item1 liftZip item2
```