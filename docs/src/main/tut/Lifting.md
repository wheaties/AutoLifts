Lifters:

//TODO: here's the idea
1. Go over what "lifting is"
2. Show case each and every function on the implicit class
3. Show the function auto lifters

//Crap, should split this into two different pages.
// 1. the syntax on objects
// 2. the lifters on functions

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
```tut
import autolift._
import AutoLift._
import scalaz._
import Scalaz._

def apOver(optList: List[Option[Int]], f: Option[Int => Int]) = optList liftAp f
```

## 4. liftFoldLeft and liftFoldRight

## 5. liftFold

## 6. liftFoldMap

## 7. liftFoldAt