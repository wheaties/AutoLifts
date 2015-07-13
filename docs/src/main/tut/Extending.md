---
layout: default
title: Extentions
---
# Extending AutoLifts

In order to have classes and traits which are custom to a code base work with AutoLifts, several type classes need to be created. Not all methods require all type classes. The following is a rough guideline:

 * Anything with the "lift" keyword will need a `Functor`
 * Anything with the "fold" keyword will need a `Foldable` (or `Traverseable`)
 * Any result type of a "fold" method will need a `Monoid`
 * "Ap" implies an `Apply` (or `Applicative`)
 * "FlatMap" implies `Bind` (or `Monad`)

## Example

Imagine the following code exists in a project:

```tut:silent
trait Status[+A]{
  def map[B](f: A => B): Status[B]
  def flatMap[B](f: A => Status[B]): Status[B]
}

case class Open[+A](value: A) extends Status[A]{
  def map[B](f: A => B): Status[B] = Open(f(value))
  def flatMap[B](f: A => Status[B]): Status[B] = f(value)
}

case object Closed extends Status[Nothing]{
  def map[B](f: Nothing => B) = this
  def flatMap[B](f: Nothing => Status[B]) = this
}
```

In order to use `liftMap` and `liftFlatMap` the following implicit class should be added to the code base:

```tut:silent
import scalaz.Bind

implicit object StatusBind extends Bind[Status]{
  def bind[A, B](fa: Status[A])(f: A => Status[B]): Status[B] = fa.flatMap(f)
  def map[A, B](fa: Status[A])(f: A => B): Status[B] = fa.map(f)
}
```

Only one type class `Bind` needs to be added in this case because a `Bind` is a `Functor`. Coincidentally, `Bind` is also an `Apply`, unlocking the `liftAp` method as a bonus.