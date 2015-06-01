AutoLifts
=========

A dependently typed library for auto lifting and auto mapping of functions. Still a Work In Progress. Code is not available yet for download.

[![Build Status](https://secure.travis-ci.org/wheaties/AutoLifts.png)](http://travis-ci.org/wheaties/AutoLifts)

## Lifters
A generalization on the concept of lifting such that functions can be lifted to arbitrarily deep nestings and stacks of Functors, Applicatives, Monads and Traversables. Or, put another way, adds a more powerful `map`, `flatMap`, and `fold` that figures out where the best application site is based upon the type of the function.

```
>> val addOne = { x: Int => x + 1 }

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

Finally includes a number of functions conversion calls which wrap a given function instance into an auto-lifting context. For instance, `liftIntoF` will lift a function into a given context:

```
>> def any2Int(x: Any) = x match{
	case i: Int => i
	case _ => 1
}

>> val into = liftIntoF[List](any2Int _)
>> into(Option(List(Option(1), Option(2))))
res3: Option[List[Int]] = Some(List(1, 1))
```

##Folders
A generalization on the concept of folding. Any traversable object with a defined instance of a `Foldable` yields folds over arbitrarily deep nestings.

```
>> val nested = List(List("1", "2", "3"), Nil, List("4", "5", "6"))
>> nested.foldWith{ x: String => x.toInt }
res4: Int = 21
```

##Transformers
Boilerplate reducing auto derivation of a `map` and `ap` on a stack of Monads such that they behave akin to the way a MonadTransformer might be expected to behave. Differs from the generalization on lifting in that application of functions are forced onto the inner most contained type instead on the first applicable type.

```
>> val doubleOL = Option(List({ x: Int => 2*x }))
>> val in = Option(List(1))

>> in transformAp doubleOL
res0: Option[List[Int]] = Some(List(1))
```

Work still on going for `flatMap`.