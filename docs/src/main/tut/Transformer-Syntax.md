---
layout: post
title: Transformer Syntax
---
# Transformer Syntax

The `Transformers` group provides for some convenience syntax upon any nested instance of two or more higher-kinded types which have a defined `Functor`. The following methods are added via an implicit extension class:

 * transformMap - an auto-lifting `map` which requires that the type of function can only be applied to the inner most type
 * transformAp - an auto-lifting `ap` which requires that the type of the function be contained within the same type nesting

## transformMap

This method is analogous to `map` except that it works in the context of the nested type. Like a regular definition of `map` which is `map[B](f: A => B): L[B]` on some type `L[_]`, `transformMap` is defined as `map[B](f: A => B): F1[F2...Fn[B]...]]` on a type `F1[F2...Fn[_]...]]`. This is almost identical in nature to `liftMap` of the `Lifters` group with the caveat that the type of the function must work on `A` and not one of the nested types `Fn[_]`, i.e. given a `List[Option[A]]` the function could not be of the form `Option[A] => B`. It works by successively calling `map` on each type until the final application of the function.

To demonstrate, the following:

```tut
val lo = List(Option(1), Option(2), None)
val out = lo.map{ x: Option[Int] =>
  x.map(_ + 1)
}
```

is equivalent to

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val lo = List(Option(1), Option(2), None)
val out = lo transformMap { x: Int => x+ 1 }
```

## transformAp

This method is analogous to `ap` from `Applicative` except that it work in the context of nested types. Given the signature of `ap[B](fa: F[A])(f: F[A => B]): F[B]`, `transformAp` has the synthetic signature `transformAp[B](fa: F1[F2...Fn[A]...]])(f: F1[F2...Fn[A => B]...]])`.

To demonstrate, the following:

```tut
import scalaz._
import Scalaz._

val f = List(Option({ x: Int => x + 1 }), None)
val lo = List(Option(1), Option(2), None)
val apL = implicitly[Applicative[List]]
val apO = implicitly[Applicative[Option]]
val out = apL.ap(lo){
  apL.map(f){ x: Option[Int => Int] =>
    apO.ap(_: Option[Int])(x)
  }
}
```

is equivalent to

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val f = List(Option({ x: Int => x + 1 }), None)
val lo = List(Option(1), Option(2), None)
val out = lo transformAp f
```