AutoLifts
=========

A dependently typed library for auto lifting and auto mapping of functions. Still a Work In Progress. Code is not available yet for download.

## AutoLift
A generalization on the concept of lifting such that functions can be lifted to arbitrarily deep nestings and stacks of Functors and/or Monads. Or, put another way, adds a more powerful `map` and `flatMap` that figures out where the best application site is based upon the type of the function.

```
>> def addOne(x: Int) = x + 1

>> val in = Option(List(1, 2, 3))
>> in liftMap addOne
res0: Option[List[Int]] = Some(List(2, 3, 4))
```

Also included are generalized `liftF` and `liftM` functions such that each wraps a function into an auto lifting context:

```
>> val liftedOne = liftF(addOne)
>> liftedOne(List(1, 2, 3))
res1: List[Int] = List(2, 3, 4)

>> liftedOne(List(Option(1), None))
res2: List[Option[Int]] = List(Some(2), None)
```

Finally includes `liftIntoF` and `liftIntoM` functions such that each wrapped function will be auto lifted into a specific context within a stack of Monads/Functors:

```
>> def any2Int(x: Any) = x match{
	case i: Int => i
	case _ => 1
}

>> val into = liftIntoF[List](any2Int)
>> into(Option(List(Option(1), Option(2))))
res3: Option[List[Int]] = Some(List(1, 1))
```

##AutoMap
A type driven `map`/`flatMap` combination which choses the correct mapping based upon the types of the function and the applied object.

```
>> def onList[B](f: Int => B)(implicit dm: DepMap[List[Int], Int => B]): dm.Out = List(1, 2, 3) autoMap f

>> onList({x: Int => List(x+1)})
res0: List[Int] = List(2, 3, 4)

>> onList(_ + 1)
res0: List[Int] = List(2, 3, 4)
```

Also included is an `auto` function which places a given function into an auto mapping context

```
>> def doubleList(x: Int) = List(x, x)

>> val dm = auto(doubleList)
>> dm(Option(2))
res1: Option[List[Int]] = Some(List(2, 2))

>> dm(List(2, 3))
res2: List[Int] = List(2, 2, 3, 3)
```

##TransformMap
Boilerplate reducing auto derivation of a `map` and `ap` on a stack of Monads such that they behave as if they were a MonadTransformer. Differs from the generalization on lifting in that application of functions are forced onto the inner most contained type instead on the first applicable type.

```
>> def double(x: Option[List[Int]]) = 2*x

>> val in = Option(List(1))
>> in transformAp double
res0: Option[List[Int]] = Some(List(1))
```

Work still on going for `flatMap`.