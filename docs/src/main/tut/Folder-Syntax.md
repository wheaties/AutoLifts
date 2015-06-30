---
layout: post
title: Folder Syntax
---
# Folder Syntax

The Folderes package provides for some convenience syntax upon any instance of a higher-kinded type which has a defined `Foldable`. The following methods are added via an implicit extension class:

 * foldWith - An auto-folding `foldMap`
 * foldAny - An auto-folding `exists`
 * foldAll - An auto-folding `forall`
 * foldComplete - An auto-folding `fold`
 * foldOver - An auto-folding `fold` and `foldMap` combination

There is no requirement that the type signature of the syntax target contain multiply nested types. Most methods require additional type classes to be in existance in order to be used, such as `Monoid` on `foldComplete`.

## foldWith

FoldWith is an auto-folding `foldMap` with the ability to auto detect the first type within a nested type structure for which the function can be applied based on the signature of the function. That is, it works by calling successive  and nested `foldMap` until the function is applied. This requires that every type up to but not including the type upon which the funciton is applied has an instance of `Foldable`. The result type of the function application must have an instance of `Monoid`.

To demonstrate, given the following code:

```tut
val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldLeft(0){
  _ + _.foldLeft(0)(_ + _ % 2)
}
```

using `foldWith` it could be rewritten in a single line:

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldWith{ x: Int => x % 2 }
```

## foldAny

FoldAny is an auto-folding version of `exists` with the ability to auto detect the first type within a nested type structure for which the function can be applied based upon the signature of the function. It works by calling successive `any` from the `Foldable` type class. If you aren't familiar with `any` it is equivalent to `foldRight(false)(_ || p(_))`.

To demonstrate, given the following code:

```tut
val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldLeft(false){
  _ || _.exists(_ > 3)
}
```

using `foldAny` it could be written in a single line:

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldAny{ x: Int => x > 3 }
```

## foldAll

FoldAll is the corollary to `foldAny`. It is an auto-folding version of `forall`, again with the ability to auto detect the first type within a nested type structure for which the function can be applied based on the type signature of the function. It works by making successive calls to `all` from the `Foldable` type class. If you aren't familiar with `all` it is equivalent to `foldRight(true)(_ && p(_))`.

To demonstrate, given the following code:

```tut
val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldLeft(true){
  _ && _.forall(_ > 3)
}
```

using `foldAny` it could be written in a single line:

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldAll{ x: Int => x > 3 }
```

## foldComplete

This is equivalent to an auto-folding `fold`. That is, given a nested type structure ultimately containing a type `A` with a defined `Monoid`, it will fold until it produces a single value of `A`. It works by successively calling `foldMap` until the very last type at which point it calls `fold`.

To demonstrate, given the following code:

```tut
val sl = List(Set(1, 2, 3), Set(4, 3))
val out = sl.foldLeft(0){
  _ + _.foldLeft(0)(_ + _)
}
```

using `foldComplete` it could be written in a single line:

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val sl = List(Set(1, 2, 3), Set(4, 3))
val out = sl.foldComplete
```

## foldOver

FoldOver is analogous to `foldComplete` except that the location of the final `fold` is given an explicit upper higher-kinded type bound within the nested type structure. That is, if some type `A` and some type `L[_]` both had defined `Monoid` then calling `foldComplete` on a type structure of `P[L[A]]` would produce a type of `L[A]` while calling `foldOver[L]` would produce a result of type `A`. Otherwise, it works in the exact same manner as `foldComplete`.

To demonstrate, given the following code:

```tut
val ol = List(Option(1), Option(2), Option(3))
val out = ol.foldLeft(0){ 
  _ + _.getOrElse(0)
}
```

using `foldOver` it could be written as:

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val ol = List(Option(1), Option(2), Option(3))
val out = ol.foldOver[Option]
```