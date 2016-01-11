---
layout: default
title: LiftM
---

The `liftM2`, `liftM3`, `liftM4` and `liftM5` set of operations are analogous to calling a function from within a for-comprehension or nested sets of for-comprehensions. They are based on the Haskell [liftM](https://wiki.haskell.org/Lifting#Monad_lifting) family of functions, only on steriods. The function is applied based according to the types of the arguments in relation to the function itself but does require that every nested type have the ability to be Monadically composed. In practice this means that `liftM2` can take a function of arity 2, `(A, B) => C` and apply it to objects of the form `M[A]`, `M[B]`; `M1[M2[A]]`, `M1[M2[B]]`; and up to any "n" number of nestings.

Unlike `LiftMap` there are no syntax extensions, only contexts.

Contains: *Scalaz*, *Algebird*

## Context

The given "N" context allows a function of arity "N" to work on "N" objects of the form `F[A]` where `A` may in fact be some higher-kinded type. The type structures must be equivalent up to the point of application. Given the following:

```tut
val one = Option(List(0, 1))
val two = Option(List(1, 1))

def sum(x: Int, y: Int) = x + y

val out = one.flatMap{ alist: List[Int] =>
  two.map{ blist: List[Int] =>
    alist.flatMap{ a: Int =>
      blist.map(sum(a, _))
    }
  }
}
```

the following expression is equivalent:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val one = Option(List(0, 1))
val two = Option(List(1, 1))

def sum(x: Int, y: Int) = x + y

val out = liftM2(sum)(one, two)
```