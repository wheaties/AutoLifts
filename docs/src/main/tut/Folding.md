---
layout: default
title: Folders
category: Folders
---

## Brief Introduction to Folding

Unlike lifting, folding is not a design pattern. It typically is expressed as a higher-order functions which take as input a function of two arguments and a starting value of some type and returns a value of the same type as the input value. Variations such such as the `reduce` or `scan` family of functions change the semantics of the fold slightly but can still be expressed in terms of a fold.

For instance, the `exists` function on a `List` can be rewritten in terms of a `foldLeft`:

```tut:silent
def any[A](value: List[A])(pred: A => Boolean): Boolean = value.foldLeft(false){ _ && pred(_) }
```

For a more indepth discussion of folding, see the [Haskell wiki page](https://wiki.haskell.org/Fold).

## Folding in AutoLifts

For cases where nested folds are clear cut and/or the application of a `foldMap`, 'exists' or 'forall' within a fold is clear, but the actual written code is less clear, AutoLifts beings to shine. As an example, given a list of lists of integers, determining if there exists an integer less than 1 within it could be written as:

```tut:silent
val items = List(List(1, 2, 3), List(1, 2, 3), List(1))
val out = items.exists{ 
  _.exists{ _ < 1 } 
}
```

Using the syntax extensions of AutoLifts it could be rewritten:

```tut:silent
import scalaz._
import Scalaz._
import autolift.Scalaz._

val items = List(List(1, 2, 3), List(1, 2, 3), List(1))
val out = items.foldAny{ x: Int => x < 0 }
```