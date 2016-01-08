---
layout: default
title: LiftFilter
---

`LiftFilter` is a more general `filter` with the ability to auto-deduce where a given predicate should be applied based upon the type signature of the predicate and the applicability of the type structure to which it is applied. That is, `liftFilter` can lift a predicate into the first suitable context for which it can be applied.

## Syntax Extension

 In practice, it operates as successive calls of `map` over nested types until it filters. The return type of the method is the same as the object it was fed.

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

## Context

The `liftFilter` is analogous to the `filter` method on many collection types only within an auto-lifting context. In order to work, it requires that the object acted upon have defined a `Foldable`, `Monoid` and `Applicative`. It operates by calling successive `map` operations until it is able to filter.

To demonstrate:

```tut
import autolift._
import Lifters._
import scalaz._
import Scalaz._

val lifted = liftFilter{ x: Any => x.toString.size < 2 }
val single = lifted(List(1, 10, 100))
val doubly = lifted(NonEmptyList(List(1, 10, 100)))
```