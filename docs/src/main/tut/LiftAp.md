---
layout: default
title: LiftAp
---

`Applicatives` are [a structure intermediate between a Functor and a Monad](https://hackage.haskell.org/package/base-4.8.0.0/docs/Control-Applicative.html). `LiftAp` is the correlary to `Applicative` in this library. If you don't know what an `Applicative` is, don't worry, you don't have to use it. Just know that `ap` works by taking a type `F[A]` and operates on it with a type `F[A => B]` to return a type `F[B]`. Hence, the function is "within" an `Applicative` itself.

Underneath the hood, `LiftAp` works by calling successive `map` operations on the encountered type structure until it finds a suitable place where the "function" `F[A => B]` can be applied. This allows `LiftAp` to operate on types `F[A]`, `F[G[A]]` and all the way up to `F1[F2[...Fn[A]...]]`. Unlike other operations in this library, the type application is very specific and will not work with variance or inheritance.

Contained: *Scalaz*, *Algebird*

## Syntax Extension

The syntax extension adds the method `liftAp` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val lifted = liftAp(List({ x: Int => x+1 }, { x: Int => x+4 }))
val single = lifted(List(1, 2, 3))
val doubly = lifted(Option(List(1, 2, 3)))
```

## Context

The context allows a "function" of the form `G[A => B]` to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. The context acts similar to a function providing `andThen`, `compose` and `map` functions besides an `apply`. It should be noted that each pluggable back-end supplies its own variation of the context which works with that back-end's `Applicative` and will not be compatible with other back-end contexts.

To demonstrate:

```tut
import scalaz._
import Scalaz._

def apOver(optList: List[Option[Int]], f: Option[Int => Int])(implicit ap: Apply[Option]) = 
  optList map { opt: Option[Int] => 
    ap.ap(opt)(f)
  }
```

which could be redone as

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

def apOver(optList: List[Option[Int]], f: Option[Int => Int]) = optList liftAp f
```