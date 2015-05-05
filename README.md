AutoLifts
=========

A dependently typed library for auto lifting and auto mapping of functions. Still a Work In Progress. Code is not available yet for download.

## AutoLift
A generalization on the concept of a MonadTransformer such that functions can be lifted to arbitrarily deep nestings and stacks of Functors and/or Monads. Or, put another way, adds a more powerful `map` and `flatMap` that figures out where the best application site is based upon the type of the function.

```
>> def addOne(x: Int) = x + 1

>> val in = Option(List(1, 2, 3))
>> in liftMap addOne
res0: Option[List[Int]] = Some(List(2, 3, 4))
```

##AutoMap
A type driven `map`/`flatMap` combination which choses the correct mapping based upon the types of the function and the applied object.