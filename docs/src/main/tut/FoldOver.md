---
layout: default
title: FoldOver
---

`FoldOver` is analogous to `foldComplete` except that the location of the final `fold` is given an explicit upper higher-kinded type bound within the nested type structure. That is, if some type `A` and some type `L[_]` both had defined `Monoid` then calling `foldComplete` on a type structure of `P[L[A]]` would produce a type of `L[A]` while calling `foldOver[L]` would produce a result of type `A`. Otherwise, it works in the exact same manner as `foldComplete`.

Contains: *Scalaz*, *Cats*

## Syntax Extension

The syntax extension adds the method `foldOver` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val ol = List(Option(1), Option(2), Option(3))
val out = ol.foldLeft(0){ 
  _ + _.getOrElse(0)
}
```

using `foldOver` it could be written as:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val ol = List(Option(1), Option(2), Option(3))
val out = ol.foldOver[Option]
```