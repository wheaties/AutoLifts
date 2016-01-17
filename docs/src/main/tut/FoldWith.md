---
layout: default
title: FoldWith
---

`FoldWith` is a more powerful `fold` which uses the type structure of the object and function to automatically determine correct nested level at which to begin folding. The concept behind the implementation is analogous to a traditional `fold` but recursively across the type structure with the requirement that the return type of the function have a `Monoid`. Hence, given a function of the form `A => B` it is free to act upon a structure of `F[A]`, `F1[F2[A]]` and all the way up to `F1[F2[...Fn[A]...]]` to produce a singular `B`.

Contains: *Scalaz*, *Cats*

## Syntax Extension

The syntax extension adds the method `liftMap` to any object of whose type can be expressed in the form `F[A]` where `A` may in fact be some higher-kinded type. To demonstrate, given the following code:

```tut
val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldLeft(0){
  _ + _.foldLeft(0)(_ + _ % 2)
}
```

using `foldWith` it could be rewritten in a single line:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val sl = Set(List(1, 2, 3), List(4, 3, 4))
val out = sl.foldWith{ x: Int => x % 2 }
```

## Context

The context allows the function to work on any object of the form `F[A]` where `A` may in fact be some higher-kinded type. The context acts similar to a function providing `andThen`, `compose` and `map` functions besides an `apply`. The `map` member function requires a mapping to a type which also has a `Monoid`. 

To demonstrate:

```tut
import scalaz._
import Scalaz._
import autolift.Scalaz._

val folded = foldWith{ x: String => x.toInt }
val single = folded(List("1", "2", "3"))
val many = folded(List(Set("1"), Set("2", "3")))
```