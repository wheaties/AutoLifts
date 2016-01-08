---
layout: default
title: Lifter Functions
category: Lifters
---
# Lifter Functions

The Lifters group contains several context transforming functions which form the corrolary of auto-lifting logic. Wherein lifting syntax was concerned with a specific type and arbitrary functions, the lifting functions are concerned with specific function types and arbitrarily nested types. Included in the package are the following transformations:

 * liftFoldMap - places a function into an auto-lifting context that folds
 * liftM2 - places a function of airity 2 into an auto-lifting context

All context wrappers require that the types operated on have at least a `Functor` defined for them. Several of these context wrappers require additional type classes, such a `liftFoldMap` requiring a `Foldable`.


## liftM2, liftM3

The `liftM2` is analogous to calling a function from within a for-comprehension or nested sets of for-comprehensions. It is based on the Haskell [liftM](https://wiki.haskell.org/Lifting#Monad_lifting) family of functions, only on steriods. The function is applied based according to the types of the arguments in relation to the function itself but does require that every nested type have at least a `Bind` defined.

To demonstrate:

```tut
val out = for{
	a <- List(1, 2, 3)
	b <- List(1, 2, 3)
} yield a - b
```

is identical to

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

def sub(x: Int, y: Int) = x - y

val out = liftM2(sub)(List(1, 2, 3), List(1, 2, 3))
```

The `liftM3` context producing function is equivalent to `liftM2` only for airity 3 functions.

## liftA2, liftA3

The `liftA2` is similar to `liftM2` except instead of using the power of Monadic composition to lift a function, it uses Applicatives. It is based on the Haskell [LiftA](https://wiki.haskell.org/Lifting#Applicative_lifting) family of functions, only on steroids. The function is applied based according to the types of the arguments in relation to the function itself but does require that every nested type have at least a `Apply` defined.

To demonstrate, since `List` has a Monad and thus an Applicative, `liftA2` should be equivalent to `liftM2`:

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

def sub(x: Int, y: Int) = x - y

val out = liftA2(sub)(List(1, 2, 3), List(1, 2, 3))
```

The `liftA3` context producing function is equivalent to `liftA2` only for airity 3 functions.