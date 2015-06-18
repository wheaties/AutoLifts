# Lifter Functions

The Lifters package contains several context transforming functions which form the corrolary of auto-lifting logic. Wherein lifting syntax was concerned with a specific type and arbitrary functions, the lifting functions are concerned with specific function types and arbitrarily nested types. Included in the package are the following transformations:

 * liftF - places a function into an auto-lifting context
 * liftAp - takes a type of the form `L[A => B]` and puts it into an auto-lifting context
 * liftM - takes a function of the form `A => M[B]` and places it into an auto-lifting `flatMap` context
 * liftIntoF - places a function into an auto-lifting context defined by some type `L[_]`
 * liftFoldMap - places a function into an auto-lifting context that folds

All context wrappers require that the types operated on have at least a `Functor` defined for them. Several of these context wrappers require additional type classes, such a `liftFoldMap` requiring a `Foldable`.

## liftF

The `liftF` function is analogous to the `lift` method of a `Functor`. Unlike `lift` which must have a defined type `L[_]` into which it is lifted, that restriction is relaxed so that any type or nested set of types may be acted upon. Hence, given a function of the form `A => B` and having been transformed by `liftF` it is free to act upon the types `F[A]`, `F[G[A]]`, `F1[F2[...Fn[A]]]`, etc. It operates by calling successive `map` operations until it finds the first type for which `A => B` may act.

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