---
layout: default
title: Folders
category: Folders
---
# Folders

The second group of functionality that the AutoLifts library adds is is a set of function wrappers and syntax extensions for type based automatic functional folding. These components can be imported one of two ways, the first by importing all library functionality via the `AutoLift` object:

```tut:silent
import autolift._
import AutoLift._
```

and the second, by importing only the `Folders` object:

```tut:silent
import autolift._
import Folders._
```

## Brief Introduction to Folding

Unlike lifting, folding is not a design pattern. It typically is expressed as a higher-order functions which take as input a function of two arguments and a starting value of some type and returns a value of the same type as the input value. Variations such such as the `reduce` or `scan` family of functions change the semantics of the fold slightly but can still be expressed in terms of a fold.

For instance, the `exists` function on a `List` can be rewritten in terms of a `foldLeft`:

```tut:silent
def any[A](value: List[A])(pred: A => Boolean): Boolean = value.foldLeft(false){ _ && pred(_) }
```

For a more indepth discussion of folding, see the [Haskell wiki page](https://wiki.haskell.org/Fold).

## Using Folders

For cases where nested folds are clear cut and/or the application of a `foldMap`, 'exists' or 'forall' within a fold is clear, but the actual written code is less clear, AutoLifts beings to shine. As an example, given a list of lists of integers, determining if there exists an integer less than 1 within it could be written as:

```tut:silent
val items = List(List(1, 2, 3), List(1, 2, 3), List(1))
val out = items.exists{ 
  _.exists{ _ < 1 } 
}
```

or using the `Folders` it could be rewritten using syntax extensions:

```tut:silent
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val items = List(List(1, 2, 3), List(1, 2, 3), List(1))
val out = items.foldAny{ x: Int => x < 0 }
```