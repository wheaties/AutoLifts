---
layout: default
title: LiftFlatMap
---

LiftFlatMap is a more general `flatMap` with the ability to auto-deduce where a function should be applied based upon the type signature of that function in relation to some object. It is to `flatMap` what `liftMap` is to `map`. It operates like `liftMap` by calling `map` repeatedly until it reaches the point where it should call `flatMap`. Hence, it allows a function of type `A => F[B]` to operate on types like `F[A]`, `F1[F2[A]]` and beyond `F1[F2[...Fn[A]...]]`.

Contained: *Scalaz*, *Algebird*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftFlatMap` (or `liftBind` in the case of Scalaz) to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val optList = List(Option(1), None)
val out = optList map{ opt: Option[Int] =>
  opt flatMap { x: Int => Option(x+1) }
}
```

it could be rewritten

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val optList = List(Option(1), None)
val out = optList liftBind { x: Int => Option(x+1) }
```

and still produce the same exact output.

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. The context acts similar to a function providing `andThen`, `compose` and `map` functions besides an `apply`. It should be noted that each pluggable back-end supplies its own variation of the context which works with that back-end's `Monad` and will not be compatible with other back-ends.

To demonstrate:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val lifted = liftBind{ x: Int => Option(x+1) }
val single = lifted(Option(1))
val doubly = lifted(List(Some(1), None, Some(3)))
```