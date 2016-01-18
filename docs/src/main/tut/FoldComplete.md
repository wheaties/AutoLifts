---
layout: default
title: FoldComplete
---

`FoldComplete` is a more powerful `fold` which recursively folds over the type structure of the object. The concept behind the implementation is analogous to a traditional `fold`, turning types like `F[A]`, `F1[F2[A]]` and all the way up to the first instance of a `Monoid` within `F1[F2[...Fn[A]...]]`. Hence, if `F2[_]` for any type has a `Monoid`, then that is the type returned by the operation.

Contains: *Scalaz*, *Cats*

## Syntax Extension

The syntax extension adds the method `foldComplete` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
import scalaz.NonEmptyList
import scalaz.syntax.foldable._

val sl = NonEmptyList(NonEmptyList(1, 2, 3), NonEmptyList(4, 3))
val out = sl.foldLeft(0){
  _ + _.foldLeft(0)(_ + _)
}
```

using `foldComplete` it could be written in a single line:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val sl = NonEmptyList(NonEmptyList(1, 2, 3), NonEmptyList(4, 3))
val out = sl.foldComplete
```