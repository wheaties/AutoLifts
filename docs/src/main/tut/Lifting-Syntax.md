---
layout: default
title: Lifter Syntax
category: Lifters
---
# Lifter Syntax

The Lifters package provides for some convenience syntax upon any instance of a higher-kinded type which has a defined `Functor`. The following methods are added via an implicit extension class:

 * liftMap - An auto-lifting `map`
 * liftFlatMap - An auto-lifting `flatMap`
 * liftAp - An auto-lifting `Applicative`
 * liftFoldLeft - An auto-lifting `foldLeft`
 * liftFoldRight - An auto-lifting `foldRight`
 * liftFold - An auto-lifting `fold`
 * liftFoldMap - An auto-lifting `fold`, analogous to the `foldMap` of the `Foldable` type class
 * liftFoldAt - An auto-lifting `fold` constrained to a specific higher-kinded type
 * liftFilter - An auto-lifting `filter`

There is no requirement that the type signature of the syntax target contain multiply nested types. Some methods require additional type classes to be defined in order to be used, such as any `fold` variants.

## 1. liftMap

LiftMap is a more general `map` with the ability to auto-deduce where a given function should be mapped based upon the type signature of the function. Or, said another way, `liftMap` can lift a function into the first suitable context for which it can be applied. In practice, it operates as if it were successive calls of `map` over nested types that have an instance of a `Functor`. 

To demonstrate, given the following code:

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
import autolift._
import AutoLift._
import scalaz._
import Scalaz._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

val listOptFut = Future(Option(List(1, 2, 3)))
val out = listOptFut liftMap { x: Int => x.toString }
```

## 2. liftFlatMap

LiftFlatMap is a more general `flatMap` with the ability to auto-deduce where a function should be flat mapped based upon the type signature of that function. It is to `flatMap` what 'liftMap' is to 'map'. It operates like `liftMap` by calling `map` repeatedly until it reaches the point where it should call `flatMap`. It requires that each nested type have at least an instance of a `Functor` defined and that the final type should have an instance of a `Bind` (it does not require the more strict `Monad`.)

To demonstrate, given the following code:

```tut
val optList = List(Option(1), None)
val out = optList map{ opt: Option[Int] =>
  opt flatMap { x: Int => Option(x+1) }
}
```

which could be rewritten

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val optList = List(Option(1), None)
val out = optList liftFlatMap { x: Int => Option(x+1) }
```

and still produce the same exact output.

## 3. liftAp

`Applicatives` are [a structure intermediate between a Functor and a Monad](https://hackage.haskell.org/package/base-4.8.0.0/docs/Control-Applicative.html). LiftAp is the correlary to `Applicative`s in this library. If you don't know what an `Applicative` is, don't worry, you don't have to use it.

Demonstrating it use

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
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

def apOver(optList: List[Option[Int]], f: Option[Int => Int]) = optList liftAp f
```

## 4. liftFoldLeft and liftFoldRight

Both `liftFoldLeft` and `liftFoldRight` are auto-lifting forms of a fold which choose where they should be applied based upon the types of the fold function. They follow the same useage pattern as `liftMap` and `liftFlatMap` and operate by succesively calling `map` up until the point where it can call the fold.  It requires that each nested type have at least an instance of a `Functor` defined and that the final type should have an instance of a `Foldable`.

Demonstrating its use, given the following

```tut
val listOpt = Option(List(1,2,3))
val out = listOpt map { list: List[Int] =>
  list.foldLeft(0)(_ + _)
}
```

it could be rewritten

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val listOpt = Option(List(1,2,3))
val out = listOpt.liftFoldLeft(0){ (x: Int, y: Int) => x + y }
```

## 5. liftFold

Like `liftFoldLeft` and `liftFoldRight`, `liftFold` finds the first type contained within a nested set of types that has a `Monoid` defined and folds over that. For those that don't known what a `Monoid` is, [it is an algebraic structure with an associative binary operation that has an identity element](https://wiki.haskell.org/Monoid) Hence, `liftFold` is equivalent to `liftFoldLeft` but the arguments supplied via an implicit type class. Again, as with the case with all of the lifters, the outer types require a `Functor` to be defined for them while a `Foldable` defined for the most inner type.

```tut
val listOpt = Option(List(1,2,3))
val out = listOpt map { list: List[Int] =>
  list.foldLeft(0)(_ + _)
}
```

compared with

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val listOpt = Option(List(1,2,3))
val out = listOpt.liftFold
```

## 6. liftFoldMap

This function is a variant on `liftFold` such that the return type of a function must have a `Monoid` defined for it. The function is lifted until it can be operated on a type which matches the signature of the function and thus a fold commenced.

The following is equivalent

```tut
val listOpt = Option(List("1","2","3"))
val out = listOpt map { list: List[String] =>
  list.foldLeft(0)(_ + _.toInt)
}
```

compared with

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val listOpt = Option(List("1","2","3"))
val out = listOpt.liftFoldMap{x: String => x.toInt}
```

## 7. liftFoldAt

Another variant of `liftFold`, this function takes as a parameter a higher-kinded type such that the type contained within that type and within the type structure of the left hand side of the operation is a `Monoid`. This is useful for cases where there is a `Monoid` for which it is not desired to be folded over and instead something found deeper in the nested structure is preferable.

As an example:

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

val futListOpt = Future(List(Option(1), None, Option(2)))
val out = futListOpt.liftFoldAt[Option]
```

which will force the evaluation at the `Int`, bypassing both the `List`, which itself is a `Monoid` and the `Option` which by way of a recurance relation is a `Monoid`.

## 8. liftFilter

LiftFilter is a more general `filter` with the ability to auto-deduce where a given predicate should be applied based upon the type signature of the predicate and the applicability of the type structure to which it is applied. That is, `liftFilter` can lift a predicate into the first suitable context for which it can be applied and that defines a `Foldable`, `Monoid` and `Applicative` (i.e. has a `foldLeft`, a means of combining non-destructively two instances, an empty representation and a single argument constructor.) In practice, it operates as successive calls of `map` over nested types until it filters. The return type of the method is the same as the object it was fed.

To demonstrate

```tut
val optList = Option(List(2, 4, 6, 8))
val out = optList.map{ list =>
  list.filter(_ % 3 == 0)
}
```

compared with

```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

val optList = Option(List(2, 4, 6, 8))
val out = optList.liftFilter{ x: Int => x % 3 == 0 }
```