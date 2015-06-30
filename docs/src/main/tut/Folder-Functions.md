---
layout: default
title: Folder Functions
category: Folders
---
# Folder Functions

The Folders package contains several context transforming functions which form the corrolary of auto-folding logic. Wherein folding syntax was concerned with a specific type and arbitrary functions, the folding functions are concerned with specific function types and arbitrarily nested types. Included in the package are the following transformations:

 * foldWith - places a function into an auto-folding context
 * foldOver - generates an auto-folding context specific to an upper type bound within the applied nested argument structures

There is no requirement that the type signature of the arguments contain multiply nested types. Besides `Foldable` being defined, the result of the fold(s) also requires a defined `Monoid`.

## foldWith

The `foldWith` function is analogous to the `foldMap` method of a `Foldable`. Unlike the `foldMap` method which is defined for a specific type `L[_]`, `foldWith` works on any nested set of higher-kinded types for which a `Foldable` is defined. Hence, given a function of the form `A => B` where `B` has a `Monoid` defined and having been transformed by `foldWith` it is free to act on the types `F[A]`, `F`[F2...Fn[A]...]]`, etc. It operates by successively calling `foldMap` until it find the first match for which `A => B` matches.

To demonstrate:

```tut
import autolift._
import Folders._
import scalaz._
import Scalaz._

val folded = foldWith{ x: String => x.toInt }
val single = folded(List("1", "2", "3"))            // single == 6
val many = folded(List(Set("1"), Set("2", "3")))    // many == 6
```

## foldOver

Less of a context wrapper for functions and more of a function generator, `foldOver` is analogous to the `fold` method of `Foldable`. Unlike plain `fold`, `foldOver` works on arbitrarily nested types with the restriction that the folding occurs on an uppper type bound within the structure. Hence, if `G[_]`, and `A` have defined `Monoid` then `foldOver[F]` on `F[G[A]]` will produce a result of type `G[A]` while `foldOver[G]` will produce a type of `A`.

To demonstrate:

```tut
import autolift._
import Folders._
import scalaz._
import Scalaz._

val foldedL = foldOver[List]
val foldedO = foldOver[Option]
val outL = foldedL(List(Option(1), Option(2), Option(3))) // outL == Option(6)
val outO = foldedO(List(Option(1), Option(2), Option(3))) // outO == 6
```