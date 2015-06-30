---
layout: post
title: Transformers
---
# Transformers

The `Transformers` component is an attempt at boilerplate free automatic MonadTransformer syntax injection. The problem with MonadTransformers is that in order to use one for a given Monad, that transformer has to be written. Hence, in libraries such as [Scalaz](https://github.com/scalaz/scalaz) instances of `ListT` and `OptionT` expand upon the standard lib while additional data types like `State` come with a `StateT`. 

It would be nice if there were synthetic methods which could be auto-injected such that a type with two differing Monads nested could be used in the same way. That is the problem this component would like to address. The one critical missing method to achieve this is a generic `flatMap`. Thus far, only the `map` and applicative `ap` could be synthetically derived.

## What Are MonadTransformers? (Why Should I care?)

MonadTransformers are used to stack Monads atop one another in a way that allows the `map` method of `F[G[A]]` to take a type `A => B` and return a `F[G[B]]` instead of taking a `G[A] => B`. Similarly for many of the other methods such as `flatMap`, the function argument would be `A` and not `G[A`. This is a work around for the fact that Monads of different types do not compose. It manifests itself most clearly in for comprehensions:

```
import concurrent.Future

def something[A](futL: Future[List[Int]])(f: Int => A): Future[List[A]] = for{
  list <- futL
  item <- list //won't compile since "list" isn't a Future and hence, doesn't compose!
} yield f(item)
```

compared to using a MonadTransformer

```
import scalaz._
import Scalaz._
import concurrent.Future

def something[A](futL: ListT[Future, Int])(f: Int => A): ListT[Future, A] = for{
  item <- futL //direct iteration over the items within the List
} yield f(item)
```