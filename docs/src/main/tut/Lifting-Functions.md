# Lifter Functions

The Lifters package contains several context transforming functions which form the corrolary of auto-lifting logic. Wherein lifting syntax was concerned with a specific type and arbitrary functions, the lifting functions are concerned with specific function types and arbitrarily nested types. Included in the package are the following transformations:

 * liftF - places a function into an auto-lifting context
 * liftAp - takes a type of the form `L[A => B]` and puts it into an auto-lifting context
 * liftM - takes a function of the form `A => M[B]` and places it into an auto-lifting `flatMap` context
 * liftIntoF - places a function into an auto-lifting context defined by some type `L[_]`
 * liftFoldMap - places a function into an auto-lifting context that folds

All context wrappers require that the types operated on have at least a `Functor` defined for them. Several of these context wrappers require additional type classes, such a `liftFoldMap` requiring a `Foldable`.

## liftF

The `liftF` function is analogous to the `lift` method of a `Functor`. Unlike `lift` which must have a defined type `L[_]` into which it is lifted, that restriction is relaxed so that any type or nested set of types may be acted upon. Hence, given a function of the form `A => B` and having been transformed by `liftF` it is free to act upon the types `F[A]`, `F[G[A]]`, `F1[F2[...Fn[A]...]]`, etc. It operates by calling successive `map` operations until it finds the first type for which `A => B` may act.

To demonstrate:

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val lifted = liftF{ x: Int => x+1 }
val single = lifted(List(1, 2, 3))                // single == List(2, 3, 4)
val doubly = lifted(List(Some(1), None, Some(3))) // doubly == List(Some(2), None, Some(4))
```

## liftAp

The `liftAp` function is more closely related to a corrolary of `Applictive`'s `apF` function than it is of applicative lifting. It expects a function of the form `L[A => B]` but like `liftF` it is not fixed to a to only types of `L[A]` and can work on arbitrarily nested types which contain `L[A]`. It operates by calling successive `map` operations until it find the first type which may be acted upon.

To demonstrate:

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val lifted = liftAp(List({ x: Int => x+1 }, { x: Int => x+4 }))
val single = lifted(List(1, 2, 3))          // single == List(2, 3, 4, 5, 6, 7)
val doubly = lifted(Option(List(1, 2, 3)))  // doubly == Option(List(2, 3, 4, 5, 6, 7))
```

## liftM

The `liftM` function wrapper is more of an extension on `flatMap` than it is on the classical Haskell [liftM](https://wiki.haskell.org/Lifting#Monad_lifting) family of functions. Hence, it takes a function of the form `A => M[B]` and operates on an arbitrarily nested set of types which must contain a `M[A]`. It operates by calling successive `map` operations until it finds the first type is may `flatMap` over.

To demonstrate:

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val lifted = liftM{ x: Int => Option(x+1) }
val single = lifted(Option(1))                    // single == Option(2)
val doubly = lifted(List(Some(1), None, Some(3))) // doubly == List(Some(2), None, Some(4))
```

## LiftIntoF

Operating exactly like `liftF` but restricted to operating over a single higher-kinded type. This function is specifically designed to work with the type signature of functions `Function1[-T, +R]`.

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val lifted = liftIntoF[List]{ x: Any => x.toString }
val single = lifted(List(1, 2))                           // single == List("1", "2")
val doubly = lifted(Option(List(Some(1), None, Some(3)))) // doubly == Some(List("Some(1)", "None", "Some(4)"))
```

## LiftFoldMap

The `liftFoldMap` is analogous to the `foldMap` method of `Foldable` only within an auto-lifting context. In order to work, it requires that the function which is wrapped produce a type that has a defined `Monoid`. It operates by calling successive `map` operations until it finds the first type which the function matches and can be folded over.

To demonstrate:

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val lifted = liftFoldMap{ x: Any => x.toString.size }
val single = lifted(List(1, 2, 3))         // single == 6
val doubly = lifted(List(Option(1), None)) // doubly == 11
```