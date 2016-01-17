---
layout: default
title: LiftFlatten
---

`LiftFlatten` is a more generalized `flatten` which uses a reference type against a type structure to automatically determine where to apply the transformation. That is, given some type `M[_]`, a type structure of the form `F[M[M[A]]]` and a means of flattening `M[_]` returns a structure of the form `F[M[A]]`.

Unlike a more traditional lifting such as `LiftMap`, there is no associated Context as there is no direct function being lifted.

Contains: *Scalaz*, *Algebird*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftFlatten` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. This method takes an optional type parameter `M[_]`. The flatten occurs at the first instance of nested `M` and aplies to only the first pair, not subsequent pairs. If no type parameter is given, Scalac will infir the first type to which is may be applied. 

To demonstrate, the following code:

```tut
val in = Option(List(List(1), List(2), List(3)))
val out = in.map{
	_.flatten
}
```

is equivalent to

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val in = Option(List(List(1), List(2), List(3)))
val out = in.liftFlatten
```