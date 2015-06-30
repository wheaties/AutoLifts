---
layout: default
title: AutoLifts
---
# AutoLifts

The AutoLift library is about enhancing the experience of using Scala such that it allows code to be written which arbitrarily nests basic types and use more complex type patterns without having to resort to the nuclear approach of Monad Transformers. Moreover, all attempts have been made to hide the complexities of the implementation as well as any direct dependencies. Users do not have to use or even understand the ideas behind Scalaz to enjoy the benefits.

All code examples contained in these documents are checked at compile time using the [tut SBT Plugin](https://github.com/tpolecat/tut). It's a living, breathing document. Pull requests are appreciated to help make the content more approachable.

## How to Use

Using the library is as simple as adding the dependency to the project. Current release version is 0.1:

```
libraryDependencies += "com.github.wheaties" %% "autolift" % "0.1"
```

Then in any file that would like to take advantage of the syntax extensions and/or context wrappers, add four imports:

```
import autolifts._
import AutoLifts._
import scalaz._
import Scalaz._
```

Scalaz will come as an indirect dependency.

## Do I Need to Know Category Theory to Use This Library?

No, not at all. In fact, other than the imports, that should be the only place you see something like Scalaz appear. Your method signatures and code should be totally unaffected. Behind the scenes, AutoLifts does use Scalaz but strives to make sure that it does not impose that use on AutoLift users.
