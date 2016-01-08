---
layout: default
title: Lifter Syntax
category: Lifters
---
# Lifter Syntax

The Lifters package provides for some convenience syntax upon any instance of a higher-kinded type which has a defined `Functor`. The following methods are added via an implicit extension class:

 * liftFold - An auto-lifting `fold`
 * liftFoldMap - An auto-lifting `fold`, analogous to the `foldMap` of the `Foldable` type class
 * liftFoldAt - An auto-lifting `fold` constrained to a specific higher-kinded type
 * liftFilter - An auto-lifting `filter`

There is no requirement that the type signature of the syntax target contain multiply nested types. Some methods require additional type classes to be defined in order to be used, such as any `fold` variants.

