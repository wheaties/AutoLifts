---
layout: default
title: LiftA
---

The `liftA2`, `liftA3`, `liftA4` and `liftA5` set of functions are similar to the `liftM2`, etc. family of functions. Instead of using the Monadic composition to lift a function, they use Applicative composition. They are based on the Haskell [LiftA](https://wiki.haskell.org/Lifting#Applicative_lifting) family of functions, only on steroids. The function is applied based according to the types of the arguments in relation to the function itself but does require that every nested type have the ability to be Applicatively composed. In practice this means that `liftA2` can take a function of arity 2, `(A, B) => C` and apply it to objects of the form `M[A]`, `M[B]`; `M1[M2[A]]`, `M1[M2[B]]`; and up to any "n" number of nestings.

Unlike `LiftMap` there are no syntax extensions, only contexts.

Contains: *Scalaz*, *Cats*

## Context

For a given "N" context, a function of arity "N" will be applied to "N" objects of the form `F[A]` where `A` may in fact be some higher-kinded type. The type structures must be equivalent up to the point of application. To demonstrate the use case:


```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val one = Option(List(0, 1))
val two = Option(List(1, 1))

def sum(x: Int, y: Int) = x + y

val out = liftA2(sum)(one, two)
```

should be technically equivalent to the results of the `liftM2` example, as a `List` is a `Monad` and thus an `Applicative`.