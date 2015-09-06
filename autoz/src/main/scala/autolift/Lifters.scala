package autolift

import scalaz.{Functor, Apply, Applicative, Bind, Foldable, Monoid}

object Lifters extends Lifters

trait Lifters extends LiftFunctions with LiftImplicits

trait LiftImplicits{
	/** Implicit explosing methods on any type constructor with a valid Functor which provides automatic function 
	 *  lifting based upon the type of the function.
	 *
	 * @param fa An instance of `F[A]`.
	 * @tparam F A type constructor for which an instance of a `Functor` exists.
	 * @tparam A The type within `F`.
	 */
	implicit class LifterOps[F[_]: Functor, A](fa: F[A]){
		def liftMap[Function](f: Function)(implicit lift: LiftF[F[A], Function]): lift.Out = lift(fa, f)

		def liftAp[Function](f: Function)(implicit lift: LiftAp[F[A], Function]): lift.Out = lift(fa, f)

		def liftFlatMap[Function](f: Function)(implicit lift: LiftB[F[A], Function]): lift.Out = lift(fa, f)

		def liftFoldLeft[Function, Z](z: Z)(f: Function)(implicit lift: LiftFoldLeft[F[A], Function, Z]): lift.Out = 
			lift(fa, f, z)

		def liftFoldRight[Function, Z](z: Z)(f: Function)(implicit lift: LiftFoldRight[F[A], Function, Z]): lift.Out = 
			lift(fa, f, z)

		def liftFold(implicit lift: LiftFold[F[A]]): lift.Out = lift(fa)

		def liftFoldMap[Function](f: Function)(implicit lift: LiftFoldMap[F[A], Function]): lift.Out = lift(fa, f)

		def liftFoldAt[M[_]](implicit fold: LiftFoldAt[M, F[A]]): fold.Out = fold(fa)

		def liftFilter[Function](f: Function)(implicit lift: LiftFilter[F[A], Function]): F[A] = lift(fa, f)
	}
}

//TODO: split into another file, a la ops, syntax.
//TODO: These would actually be a lot easier to handle if funcion types weren't single type expressions. Those make sense in LifterOps only.
//TODO:    ...and functions could be chained.
trait LiftFunctions{ //These are autolifting contexts
	def liftIntoF[F[_]] = new LIFMaker[F]

	sealed class LIFMaker[F[_]]{
		def apply[Function](f: Function)(implicit ev: Functor[F]) = new LiftIntoFunctor[F, Function](f)
	}

	sealed class LiftIntoFunctor[F[_]: Functor, Function](f: Function){
		def apply[That](that: That)(implicit into: LiftIntoF[F, That, Function]): into.Out = into(that, f)
	}

	def liftF[Function](f: Function) = new LiftedF(f)

	sealed class LiftedF[Fn](protected[autolift] val f: Fn){
		type This = LiftedF[Fn]

		def andThen[That](that: That)(implicit comp: LiftCompose[This, That]): comp.Out = comp(this, that)

		def compose[That](that: That)(implicit comp: LiftCompose[That, This]): comp.Out = comp(that, this)

		//def map[C](f: B => C): LiftedF[A, C]

		def apply[That](that: That)(implicit lift: LiftF[That, Fn]): lift.Out = lift(that, f)
	}

	object LiftedF{
		implicit def compose[A, B, C >: B, D] = new LiftCompose[LiftedF[A => B], LiftedF[C => D]]{
			type Out = LiftedF[A => D]

			def apply(left: LiftedF[A => B], right: LiftedF[C => D]) = new LiftedF(left.f andThen right.f)
		}
	}

	def liftAp[Function](f: Function) = new LiftedAp(f)

	sealed class LiftedAp[Fn](protected[autolift] val f: Fn){
		type This = LiftedAp[Fn]

		def andThen[That](that: That)(implicit comp: LiftCompose[This, That]): comp.Out = comp(this, that)

		def compose[That](that: That)(implicit comp: LiftCompose[That, This]): comp.Out = comp(that, this)

		//def map[C](f: B => C): LiftedAp[A, C, F]

		def apply[That](that: That)(implicit lift: LiftAp[That, Fn]): lift.Out = lift(that, f)
	}

	object LiftedAp{
		implicit def compose[A, B, C >: B, D, F[_]](implicit ap: Apply[F]) =
			new LiftCompose[LiftedAp[F[A => B]],LiftedAp[F[C => D]]]{
				type Out = LiftedAp[F[A => D]]

				def apply(left: LiftedAp[F[A => B]], right: LiftedAp[F[C => D]]) = new LiftedAp(
					ap.ap(left.f)(
						ap.map(right.f){ 
							y: (C => D) => { x: (A => B) => x andThen y } 
						}
					)
				)
			}
	}

	def liftM[Function](f: Function) = new LiftedM(f)

	sealed class LiftedM[Fn](protected[autolift] val f: Fn){
		type This = LiftedM[Fn]

		def andThen[That](that: That)(implicit comp: LiftCompose[This, That]): comp.Out = comp(this, that)

		def compose[That](that: That)(implicit comp: LiftCompose[That, This]): comp.Out = comp(that, this)

		//def map[C](f: B => C): LiftedM[A, C, M]

		def apply[That](that: That)(implicit lift: LiftB[That, Fn]): lift.Out = lift(that, f)
	}

	object LiftedM{
		implicit def compose[A, B, C >: B, D, M[_]](implicit bind: Bind[M]) =
			new LiftCompose[LiftedM[A => M[B]], LiftedM[C => M[D]]]{
				type Out = LiftedM[A => M[D]]

				def apply(left: LiftedM[A => M[B]], right: LiftedM[C => M[D]]) = new LiftedM({
					x: A => bind.bind(left.f(x))(right.f)
				})
			}
	}

	def liftFoldMap[Function](f: Function) = new LiftedFoldMap(f)

	sealed class LiftedFoldMap[Fn](protected[autolift] val f: Fn){
		type This = LiftedFoldMap[Fn]

		def andThen[That](that: That)(implicit comp: LiftCompose[This, That]): comp.Out = comp(this, that)

		def compose[That](that: That)(implicit comp: LiftCompose[That, This]): comp.Out = comp(that, this)

		//def map[C](f: B => C)(implicit m: Monoid[C]): LiftedFoldMap[A, C]

		def apply[That](that: That)(implicit lift: LiftFoldMap[That, Fn]): lift.Out = lift(that, f)
	}

	object LiftedFoldMap{
		implicit def compose[A, B, C >: B, D](implicit m: Monoid[D]) =
			new LiftCompose[LiftedFoldMap[A => B], LiftedFoldMap[C => D]]{
				type Out = LiftedFoldMap[A => D]

				def apply(left: LiftedFoldMap[A => B], right: LiftedFoldMap[C => D]) = new LiftedFoldMap(left.f andThen right.f)
			}
	}

	def liftFilter[Function](f: Function) = new LiftedFilter(f)

	sealed class LiftedFilter[Function](f: Function){
		def apply[That](that: That)(implicit lift: LiftFilter[That, Function]): That = lift(that, f)
	}
}

trait LiftCompose[F, G] extends DFunction2[F, G]

/**
 * Type class supporting mapping of a function over a specific higher-kinded type within a nested type constructor.
 *
 * @author Owein Reese
 *
 * @tparam F the higher-kinded type to lift the function into.
 * @tparam Obj the type over which the function will be lifted.
 * @tparam Function the type of the function which will be lifted.
 */
sealed trait LiftIntoF[F[_], Obj, Function] extends DFunction2[Obj, Function]

object LiftIntoF extends LowPriorityLiftIntoF{
	def apply[F[_], Obj, Function](implicit lift: LiftIntoF[F, Obj, Function]): Aux[F, Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F, F[A], C => B, F[B]] =
		new LiftIntoF[F, F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityLiftIntoF{
	type Aux[F[_], Obj, Function, Out0] = LiftIntoF[F, Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G[_], In, Function](implicit functor: Functor[G], lift: LiftIntoF[F, In, Function]): Aux[F, G[In], Function, G[lift.Out]] =
		new LiftIntoF[F, G[In], Function]{
			type Out = G[lift.Out]

			def apply(gin: G[In], f: Function) = functor.map(gin){ in: In => lift(in, f) }
		}
}

/**
 * Type class supporting the mapping over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj the type to be lifted into.
 * @tparam Function the function to be lifted.
 */
sealed trait LiftF[Obj, Function] extends DFunction2[Obj, Function]

object LiftF extends LowPriorityLiftF {
	def apply[Obj, Function](implicit lift: LiftF[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new LiftF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityLiftF{
	type Aux[Obj, Function, Out0] = LiftF[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftF[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftF[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

/**
 * Type class supporting the applicative mapping of a type over another type of arbitrary nested type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type of object to be lifted into.
 * @tparam Funciton The type of function to be lifted.
 */
sealed trait LiftAp[Obj, Function] extends DFunction2[Obj, Function]

object LiftAp extends LowPriorityLiftAp {
	def apply[Obj, Function](implicit lift: LiftAp[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new LiftAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}

trait LowPriorityLiftAp{
	type Aux[Obj, Function, Out0] = LiftAp[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftAp[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftAp[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

/**
 * Type class supporting flat mapping a function over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Funciton The function to be lifted.
 */
sealed trait LiftB[Obj, Function] extends DFunction2[Obj, Function]

object LiftB extends LowPriorityLiftB {
	def apply[Obj, Function](implicit lift: LiftB[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M[A], C => M[B], M[B]] =
		new LiftB[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = bind.bind(fa)(f)
		}
}

trait LowPriorityLiftB{
	type Aux[Obj, Function, Out0] = LiftB[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftB[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftB[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

/**
 * Type class supporting foldLeft over an arbitrary nesting of type constructors given an initial value and a function.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The 2-airy function to be lifted.
 * @tparam Z The initial value of the fold.
 */
trait LiftFoldLeft[Obj, Function, Z] extends DFunction3[Obj, Function, Z]

object LiftFoldLeft extends LowPriorityLiftFoldLeft{
	def apply[FA, Function, Z](implicit lift: LiftFoldLeft[FA, Function, Z]): Aux[FA, Function, Z, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (B, C) => B, B, B] =
		new LiftFoldLeft[F[A], (B, C) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (B, C) => B, z: B) = fold.foldLeft(fa, z)(f)
		}
}

trait LowPriorityLiftFoldLeft{
	type Aux[FA, Function, Z, Out0] = LiftFoldLeft[FA, Function, Z]{ type Out = Out0 }

	implicit def recur[F[_], G, Function, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Function, Z]): Aux[F[G], Function, Z, F[lift.Out]] =
		new LiftFoldLeft[F[G], Function, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

/**
 * Type class supporting foldRight over an arbitrary nesting of type constructors given an initial value and a function.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The 2-airy function to be lifted.
 * @tparam Z The initial value of the fold.
 */
trait LiftFoldRight[FA, Function, Z] extends DFunction3[FA, Function, Z]

object LiftFoldRight extends LowPriorityLiftFoldRight{
	def apply[FA, Function, Z](implicit lift: LiftFoldRight[FA, Function, Z]): Aux[FA, Function, Z, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (C, => B) => B, B, B] =
		new LiftFoldRight[F[A], (C, => B) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (C, => B) => B, z: B) = fold.foldRight(fa, z)(f)
		}
}

trait LowPriorityLiftFoldRight{
	type Aux[FA, Function, Z, Out0] = LiftFoldRight[FA, Function, Z]{ type Out = Out0 }

	implicit def recur[F[_], G, Function, Z](implicit functor: Functor[F], lift: LiftFoldRight[G, Function, Z]): Aux[F[G], Function, Z, F[lift.Out]] =
		new LiftFoldRight[F[G], Function, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

/**
 * Type class supporting folding over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 */
trait LiftFold[Obj] extends DFunction1[Obj]

object LiftFold extends LowPriorityLiftFold{
	def apply[FA](implicit lift: LiftFold[FA]): Aux[FA, lift.Out] = lift

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new LiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityLiftFold{
	type Aux[FA, Out0] = LiftFold[FA]{ type Out = Out0 }

	implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
		new LiftFold[F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

/**
 * Type class supporting fold over an arbitrary nesting of type constructors given a function which maps initial types to
 * some other type defined with a Monoid.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The function to be used to map values.
 */
trait LiftFoldMap[FA, Function] extends DFunction2[FA, Function]

object LiftFoldMap extends LowPriorityLiftFoldMap{
	def apply[FA, Function](implicit lift: LiftFoldMap[FA, Function]): Aux[FA, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new LiftFoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityLiftFoldMap{
	type Aux[FA, Function, Out0] = LiftFoldMap[FA, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftFoldMap[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftFoldMap[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

/**
 * Type class supporting folding over a nested type constructor up to and including a type constructor.
 *
 * @author Owein Reese
 *
 * @tparam F The type at which to stop folding.
 * @tparam Obj The type over which to lift the folding.
 */
trait LiftFoldAt[F[_], Obj] extends DFunction1[Obj]

object LiftFoldAt extends LowPriorityLiftFoldAt{
	def apply[F[_], Obj](implicit fold: LiftFoldAt[F, Obj]): Aux[F, Obj, fold.Out] = fold

	implicit def base[F[_], A](implicit fold: FoldComplete[F[A]]): Aux[F, F[A], fold.Out] =
		new LiftFoldAt[F, F[A]]{
			type Out = fold.Out

			def apply(fa: F[A]) = fold(fa)
		}
}

trait LowPriorityLiftFoldAt{
	type Aux[F[_], Obj, Out0] = LiftFoldAt[F, Obj]{ type Out = Out0 }

	implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFoldAt[F, H]): Aux[F, G[H], G[fold.Out]] =
		new LiftFoldAt[F, G[H]]{
			type Out = G[fold.Out]

			def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
		}
}

/**
 * Typeclass supporting filtering a set of values within a nested type constructor.
 * 
 * @author Owein Reese
 * 
 * @tparam Obj The object over which to filter
 * @tparam Function The predicate which determines if a value is included in the final result
 */
trait LiftFilter[Obj, Function] extends ((Obj, Function) => Obj)

object LiftFilter extends LowPriorityLiftFilter{
	def apply[Obj, Function](implicit lift: LiftFilter[Obj, Function]) = lift

	implicit def base[F[_], A, B >: A](implicit fold: Foldable[F], m: Monoid[F[A]], ap: Applicative[F]) =
		new LiftFilter[F[A], B => Boolean]{
			def apply(fa: F[A], pred: B => Boolean) = fold.foldRight(fa, m.zero){
				(a, res) => if(pred(a)) m.append(ap.pure(a), res) else res
			}
		}
}

trait LowPriorityLiftFilter{
	implicit def recur[F[_], G, Function](implicit lift: LiftFilter[G, Function], functor: Functor[F]) =
		new LiftFilter[F[G], Function]{
			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}