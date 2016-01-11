---
layout: default
title: AutoLifts
---
# AutoLifts

The AutoLift library is about enhancing the experience of using Scala such that it allows code to be written which contains nested types and use more complex type patterns without having to resort to hand written boilerplate, indirection helper functions or the nuclear option, Monad Transformers. Moreover, all attempts have been made to hide the complexities of the implementation as well as any direct dependencies. Users do not have to use or even understand the ideas behind the underlying libraries to enjoy the benefits.

All code examples contained in these documents are checked at compile time using the [tut SBT Plugin](https://github.com/tpolecat/tut). It's a living, breathing document. Pull requests are appreciated to help make the content more approachable.

## How to Use

The library is divided into several modules. The core module is the skeletal framework which contains unimplemented type classes and components built around those type classes. Accompanying the core are the type class provider libraries, each independently providing a non-unique subset of core type classes. It is recommended that code use only one of the type class providers, otherwise implicit resolution can potentially become difficult.

As an example, add the following to the build definition:

```scala
libraryDependencies ++= Seq(
	"com.github.wheaties" %% "autolift-core" % "0.4"
	"com.github.wheaties" %% "autolift-scalaz" % "0.4"
)
```

Scalaz will come as an indirect dependency.

Then in any file that would like to take advantage of the syntax extensions and/or context wrappers, add four imports:

```scala
import scalaz._
import Scalaz._
import autolift.Scalaz._
```

## Do I Need to Know Category Theory to Use This Library?

No, not at all. In fact, other than the imports, that should be the only place you see something like Scalaz appear. Your method signatures and code should be totally unaffected. Behind the scenes, AutoLifts does use a pluggable back-end that is functionally based but strives to make sure that it does not impose that back-end use on AutoLift users.
