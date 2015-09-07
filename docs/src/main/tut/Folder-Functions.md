---
layout: default
title: Folder Functions
category: Folders
---
# Folder Functions

The Folders package contains several context transforming functions which form the corrolary of auto-folding logic. Wherein folding syntax was concerned with a specific type and arbitrary functions, the folding functions are concerned with specific function types and arbitrarily nested types. Included in the package are the following transformations:

 * foldWith - places a function into an auto-folding context
 * foldAll - places a predicate into an auto-folding context which returns true if all folds return true
 * foldAny - places a predicate into an auto-folding context which returns true if any fold returns true

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
val single = folded(List("1", "2", "3"))
val many = folded(List(Set("1"), Set("2", "3")))
```

## foldAll

The `foldAll` function is analogous to the `forall` method on the standard collections library. It works with any nested set of `Foldable`, auto-detecting the correct place to be invoked based upon the type signatures of the predicate and applied object. It operates by making successive calls to `all` from the `Foldable` type class. If you aren't familiar with `all` it is equivalent to `foldRight(true)(_ && p(_))` where `p` is a predicate function.

To demonstrate:

```tut
import autolift._
import Folders._
import scalaz._
import Scalaz._

val folded = foldAll{ x: String => x.length < 2 }
val resTrue = folded(List("1", "2", "3"))
val resFalse = folded(List(Set("1"), Set("2", "32")))
```