---
layout: default
title: LiftMap
---

`LiftMap` is a more powerful `map` which uses the type structure of the object and function to automatically determine the correct nesting level. The concept behind the implementation of `LiftMap` is analogous to the traditional `lift` of a `Functor`, i.e. it converts a function of type `A => B` into a function of type `F[A] => F[B]`. However, unlike the tranditional implementation which is restricted to a single fixed type, here that constraint is removed. Hence, given a function of the form `A => B` it is free to act upon the types `F[A]`, `F[G[A]]`, `F1[F2[...Fn[A]...]]`, etc. 

Underneath the hood, `LiftMap` operates by calling successive `map` operations until it finds the first type for which `A => B` may act. If the argument type, `A`, is a general type such as `Any`, it will be applied to the first correct result for which it can be applied. There is currently no forcing mechanism to coerce the application point lower in the type structure.

Contained: *Scalaz*, *Algebird*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftMap` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

val listOptFut = Future(Option(List(1, 2, 3)))
val out = listOptFut map { listOpt: Option[List[Int]] =>
  listOpt map { ls: List[Int] =>
    ls map { x: Int => x.toString }
  }
}
```

using `liftMap` it could be re-expressed in a single line

```tut
import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

val listOptFut = Future(Option(List(1, 2, 3)))
val out = listOptFut liftMap { x: Int => x.toString }
```

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. The context acts similar to a function providing `andThen`, `compose` and `map` functions besides an `apply`.

To demonstrate:

```tut
import scalaz._
import Scalaz._
import autolift._
import autolift.Scalaz._

val lifted = liftMap{ x: Int => x+1 }
val single = lifted(List(1, 2, 3))
val doubly = lifted(List(Some(1), None, Some(3)))
```