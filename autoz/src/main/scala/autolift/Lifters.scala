package autolift

import scalaz.{Functor, Apply, Applicative, Bind, Foldable, Monoid, MonadPlus}

object Lifters extends Lifters

trait Lifters extends LiftFunctions with LiftImplicits

trait LiftImplicits{
	/** Implicit exposing methods on any type constructor which provides automatic function lifting based upon the 
	 *  type of the function.
	 *
	 * @param fa An instance of `F[A]`.
	 * @tparam F A type constructor
	 * @tparam A The type within `F`.
	 */
	implicit class LifterOps[F[_], A](fa: F[A]){
		def liftMap[B, C](f: B => C)(implicit lift: LiftF[F[A], B => C]): lift.Out = lift(fa, f)

		def liftAp[B, C, M[_]](f: M[B => C])(implicit lift: LiftAp[F[A], M[B => C]]): lift.Out = lift(fa, f)

		def liftFlatMap[B, C, M[_]](f: B => M[C])(implicit lift: LiftB[F[A], B => M[C]]): lift.Out = lift(fa, f)

		def liftFoldLeft[B, Z](z: Z)(f: (Z, B) => Z)(implicit lift: LiftFoldLeft[F[A], (Z, B) => Z, Z]): lift.Out = 
			lift(fa, f, z)

		def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z)(implicit lift: LiftFoldRight[F[A], (B, => Z) => Z, Z]): lift.Out = 
			lift(fa, f, z)

		def liftFold(implicit lift: LiftFold[F[A]]): lift.Out = lift(fa)

		def liftFoldMap[B, C](f: B => C)(implicit lift: LiftFoldMap[F[A], B => C]): lift.Out = lift(fa, f)

		def liftFoldAt[M[_]](implicit fold: LiftFoldAt[M, F[A]]): fold.Out = fold(fa)

		def liftFilter[B](f: B => Boolean)(implicit lift: LiftFilter[F[A], B => Boolean]): F[A] = lift(fa, f)

		def liftFlatten[M[_]](implicit lift: LiftFlatten[M, F[A]]): lift.Out = lift(fa)
	}
}

//TODO: split into another file, a la ops, syntax.
trait LiftFunctions{ //These are autolifting contexts
	def liftIntoF[F[_]] = new LIFMaker[F]

	sealed class LIFMaker[F[_]]{
		def apply[A, B](f: A => B)(implicit ev: Functor[F]) = new LiftIntoFunctor[A, B, F](f)
	}

	sealed class LiftIntoFunctor[A, B, F[_]: Functor](f: A => B){
		def andThen[C >: B, D](that: LiftIntoFunctor[C, D, F]) = that compose this

		def compose[C, D <: A](that: LiftIntoFunctor[C, D, F]) = that map f

		def map[C](g: B => C): LiftIntoFunctor[A, C, F] = new LiftIntoFunctor[A, C, F](f andThen g)

		def apply[That](that: That)(implicit into: LiftIntoF[F, That, A => B]): into.Out = into(that, f)
	}

	def liftMap[A, B](f: A => B) = new LiftedMap(f)

	sealed class LiftedMap[A, B](f: A => B){
		def andThen[C >: B, D](that: LiftedMap[C, D]) = that compose this

		def compose[C, D <: A](that: LiftedMap[C, D]) = that map f

		def map[C](g: B => C): LiftedMap[A, C] = new LiftedMap(f andThen g)

		def apply[That](that: That)(implicit lift: LiftF[That, A => B]): lift.Out = lift(that, f)
	}

	def liftAp[A, B, F[_]](f: F[A => B])(implicit ap: Apply[F]) = new LiftedAp(f)

	sealed class LiftedAp[A, B, F[_]](protected[autolift] val f: F[A => B])(implicit ap: Apply[F]){
		def andThen[C >: B, D](lf: LiftedAp[C, D, F]) = new LiftedAp(ap.ap(f)(
			ap.map(lf.f){ 
				y: (C => D) => { x: (A => B) => x andThen y } 
			}
		))

		def compose[C, D <: A](lf: LiftedAp[C, D, F]) = lf andThen this

		def map[C](g: B => C): LiftedAp[A, C, F] = new LiftedAp(ap.map(f){ _ andThen g })

		def apply[That](that: That)(implicit lift: LiftAp[That, F[A => B]]): lift.Out = lift(that, f)
	}

	def liftFlatMap[A, B, M[_]](f: A => M[B])(implicit bind: Bind[M]) = new LiftedFlatMap(f)

	sealed class LiftedFlatMap[A, B, M[_]](f: A => M[B])(implicit bind: Bind[M]){
		def andThen[C >: B, D](that: LiftedFlatMap[C, D, M]) = that compose this

		def compose[C, D <: A](that: LiftedFlatMap[C, D, M]) = that map f

		def map[C](g: B => C): LiftedFlatMap[A, C, M] = new LiftedFlatMap({ x: A => bind.map(f(x))(g) })

		def apply[That](that: That)(implicit lift: LiftB[That, A => M[B]]): lift.Out = lift(that, f)
	}

	def liftFoldMap[A, B](f: A => B)(implicit m: Monoid[B]) = new LiftedFoldMap(f)

	sealed class LiftedFoldMap[A, B](f: A => B)(implicit m: Monoid[B]){
		def andThen[C >: B, D : Monoid](that: LiftedFoldMap[C, D]) = that compose this

		def compose[C, D <: A](that: LiftedFoldMap[C, D]) = that map f

		def map[C : Monoid](g: B => C): LiftedFoldMap[A, C] = new LiftedFoldMap(f andThen g)

		def apply[That](that: That)(implicit lift: LiftFoldMap[That, A => B]): lift.Out = lift(that, f)
	}

	def liftFilter[A](f: A => Boolean) = new LiftedFilter(f)

	sealed class LiftedFilter[A](f: A => Boolean){
		def apply[That](that: That)(implicit lift: LiftFilter[That, A => Boolean]): That = lift(that, f)
	}

	def liftM2[A, B, C](f: (A, B) => C) = new LiftedM2(f)

	sealed class LiftedM2[A, B, C](f: (A, B) => C){
		def map[D](g: C => D): LiftedM2[A, B, D] = new LiftedM2((x: A, y: B) => g(f(x, y)))

		def apply[MA, MB](ma: MA, mb: MB)(implicit lift: LiftM2[MA, MB, (A, B) => C]): lift.Out = lift(ma, mb, f)
	}

	def liftM3[A, B, C, D](f: (A, B, C) => D) = new LiftedM3(f)

	sealed class LiftedM3[A, B, C, D](f: (A, B, C) => D){
		def map[E](g: D => E): LiftedM3[A, B, C, E] = new LiftedM3((x: A, y: B, z: C) => g(f(x, y, z)))

		def apply[MA, MB, MC](ma: MA, mb: MB, mc: MC)(implicit lift: LiftM3[MA, MB, MC, (A, B, C) => D]): lift.Out = 
			lift(ma, mb, mc, f)
	}

	def liftA2[A, B, C](f: (A, B) => C) = new LiftedA2(f)

	sealed class LiftedA2[A, B, C](f: (A, B) => C){
		def map[D](g: C => D): LiftedA2[A, B, D] = new LiftedA2((x: A, y: B) => g(f(x, y)))

		def apply[MA, MB](ma: MA, mb: MB)(implicit lift: LiftA2[MA, MB, (A, B) => C]): lift.Out = lift(ma, mb, f)
	}

	def liftA3[A, B, C, D](f: (A, B, C) => D) = new LiftedA3(f)

	sealed class LiftedA3[A, B, C, D](f: (A, B, C) => D){
		def map[E](g: D => E): LiftedA3[A, B, C, E] = new LiftedA3((x: A, y: B, z: C) => g(f(x, y, z)))

		def apply[MA, MB, MC](ma: MA, mb: MB, mc: MC)(implicit lift: LiftA3[MA, MB, MC, (A, B, C) => D]): lift.Out = 
			lift(ma, mb, mc, f)
	}
}

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

	implicit def plus[M[_], A, B >: A](implicit mp: MonadPlus[M]) =
		new LiftFilter[M[A], B => Boolean]{
			def apply(ma: M[A], pred: B => Boolean) = mp.filter(ma)(pred)
		}
}

trait LowPriorityLiftFilter extends LowPriorityLiftFilter1{
	implicit def foldable[F[_], A, B >: A](implicit fold: Foldable[F], m: Monoid[F[A]], ap: Applicative[F]) =
		new LiftFilter[F[A], B => Boolean]{
			def apply(fa: F[A], pred: B => Boolean) = fold.foldRight(fa, m.zero){
				(a, res) => if(pred(a)) m.append(ap.pure(a), res) else res
			}
		}
}

trait LowPriorityLiftFilter1{
	implicit def recur[F[_], G, Function](implicit lift: LiftFilter[G, Function], functor: Functor[F]) =
		new LiftFilter[F[G], Function]{
			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LiftFlatten[M[_], Obj] extends DFunction1[Obj]

object LiftFlatten extends LowPriorityLiftFlatten{
	def apply[M[_], Obj](implicit lift: LiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

	implicit def base[M[_], A](implicit bind: Bind[M]): Aux[M, M[M[A]], M[A]] =
		new LiftFlatten[M, M[M[A]]]{
			type Out = M[A]

			def apply(mma: M[M[A]]) = bind.bind(mma){ ma: M[A] => ma }
		}
}

trait LowPriorityLiftFlatten{
	type Aux[M[_], Obj, Out0] = LiftFlatten[M, Obj]{ type Out = Out0 }

	implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
		new LiftFlatten[M, F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

/**
 * Typeclass supporting lifting a function of airity 2 and applying it to the inner types through a combination of
 * flatMap and map. 
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The first object over which to apply the function.
 * @tparam Obj2 The second object over which to apply the function.
 * @tparam Function The function to be used to map values.
 */
trait LiftM2[Obj1, Obj2, Function] extends DFunction3[Obj1, Obj2, Function]

object LiftM2 extends LowPriorityLiftM2{
	def apply[Obj1, Obj2, Fn](implicit lift: LiftM2[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def base[M[_], A, B, A1 >: A, B1 >: B, C](implicit bind: Bind[M]): Aux[M[A], M[B], (A1, B1) => C, M[C]] =
		new LiftM2[M[A], M[B], (A1, B1) => C]{
			type Out = M[C]

			def apply(ma: M[A], mb: M[B], f: (A1, B1) => C) = bind.bind(ma){ a: A =>
				bind.map(mb){ b: B => f(a, b) }
			}
		}
}

trait LowPriorityLiftM2{
	type Aux[Obj1, Obj2, Fn, Out0] = LiftM2[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def recur[M[_], A, B, Fn](implicit bind: Bind[M], lift: LiftM2[A, B, Fn]): Aux[M[A], M[B], Fn, M[lift.Out]] =
		new LiftM2[M[A], M[B], Fn]{
			type Out = M[lift.Out]

			def apply(ma: M[A], mb: M[B], f: Fn) = bind.bind(ma){ a: A =>
				bind.map(mb){ b: B => lift(a, b, f) }
			}
		}
}

/**
 * Typeclass supporting lifting a function of airity 3 and applying it to the inner types through a combination of
 * flatMap and map.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The first object over which to apply the function.
 * @tparam Obj2 The second object over which to apply the function.
 * @tparam Obj3 The third object over which to apply the function.
 * @tparam Function The function to be used to map values.
 */
trait LiftM3[Obj1, Obj2, Obj3, Function] extends DFunction4[Obj1, Obj2, Obj3, Function]

object LiftM3 extends LowPriorityLiftM3{
	def apply[Obj1, Obj2, Obj3, Fn](implicit lift: LiftM3[Obj1, Obj2, Obj3, Fn]): Aux[Obj1, Obj2, Obj3, Fn, lift.Out] = lift

	implicit def base[M[_], A, B, C, A1 >: A, B1 >: B, C1 >: C, D]
		(implicit bind: Bind[M]): Aux[M[A], M[B], M[C], (A1, B1, C1) => D, M[D]] =
			new LiftM3[M[A], M[B], M[C], (A1, B1, C1) => D]{
				type Out = M[D]

				def apply(ma: M[A], mb: M[B], mc: M[C], f: (A1, B1, C1) => D) = bind.bind(ma){ a: A =>
					bind.bind(mb){ b: B => 
						bind.map(mc){ c: C => f(a, b, c) }
					}
				}
			}
}

trait LowPriorityLiftM3{
	type Aux[Obj1, Obj2, Obj3, Fn, Out0] = LiftM3[Obj1, Obj2, Obj3, Fn]{ type Out = Out0 }

	implicit def recur[M[_], A, B, C, Fn]
		(implicit bind: Bind[M], lift: LiftM3[A, B, C, Fn]): Aux[M[A], M[B], M[C], Fn, M[lift.Out]] =
			new LiftM3[M[A], M[B], M[C], Fn]{
				type Out = M[lift.Out]

				def apply(ma: M[A], mb: M[B], mc: M[C], f: Fn) = bind.bind(ma){ a: A =>
					bind.bind(mb){ b: B => 
						bind.map(mc){ c: C => lift(a, b, c, f) }
					}
				}
			}
}

/**
 * Typeclass supporting lifting a function of airity 2 and applying it to the inner types through a combination of
 * ap and map. 
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The first object over which to apply the function.
 * @tparam Obj2 The second object over which to apply the function.
 * @tparam Function The function to be used to map values.
 */
trait LiftA2[Obj1, Obj2, Function] extends DFunction3[Obj1, Obj2, Function]

object LiftA2 extends LowPriorityLiftA2{
	def apply[Obj1, Obj2, Fn](implicit lift: LiftA2[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def base[M[_], A, B, A1 >: A, B1 >: B, C](implicit ap: Apply[M]): Aux[M[A], M[B], (A1, B1) => C, M[C]] =
		new LiftA2[M[A], M[B], (A1, B1) => C]{
			type Out = M[C]

			def apply(ma: M[A], mb: M[B], f: (A1, B1) => C) = ap.ap(ma){
				ap.map(mb){ b: B => f(_, b) }
			}
		}
}

trait LowPriorityLiftA2{
	type Aux[Obj1, Obj2, Fn, Out0] = LiftA2[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def recur[M[_], A, B, Fn](implicit ap: Apply[M], lift: LiftA2[A, B, Fn]): Aux[M[A], M[B], Fn, M[lift.Out]] =
		new LiftA2[M[A], M[B], Fn]{
			type Out = M[lift.Out]

			def apply(ma: M[A], mb: M[B], f: Fn) = ap.ap(ma){
				ap.map(mb){ b: B => lift(_, b, f) }
			}
		}
}

/**
 * Typeclass supporting lifting a function of airity 3 and applying it to the inner types through a combination of
 * ap and map. 
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The first object over which to apply the function.
 * @tparam Obj2 The second object over which to apply the function.
 * @tparam Obj3 The third object over which to apply the function.
 * @tparam Function The function to be used to map values.
 */
trait LiftA3[Obj1, Obj2, Obj3, Function] extends DFunction4[Obj1, Obj2, Obj3, Function]

object LiftA3 extends LowPriorityLiftA3{
	def apply[Obj1, Obj2, Obj3, Fn](implicit lift: LiftA3[Obj1, Obj2, Obj3, Fn]): Aux[Obj1, Obj2, Obj3, Fn, lift.Out] = lift

	implicit def base[M[_], A, B, C, A1 >: A, B1 >: B, C1 >: C, D]
		(implicit ap: Apply[M]): Aux[M[A], M[B], M[C], (A1, B1, C1) => D, M[D]] =
			new LiftA3[M[A], M[B], M[C], (A1, B1, C1) => D]{
				type Out = M[D]

				def apply(ma: M[A], mb: M[B], mc: M[C], f: (A1, B1, C1) => D) = ap.ap(ma){
					ap.ap(mb){
						ap.map(mc){ c: C => b: B => a: A => f(a, b, c) }
					}
				}
			}
}

trait LowPriorityLiftA3{
	type Aux[Obj1, Obj2, Obj3, Fn, Out0] = LiftA3[Obj1, Obj2, Obj3, Fn]{ type Out = Out0 }

	implicit def recur[M[_], A, B, C, Fn](implicit ap: Apply[M], lift: LiftA3[A, B, C, Fn]): Aux[M[A], M[B], M[C], Fn, M[lift.Out]] =
		new LiftA3[M[A], M[B], M[C], Fn]{
			type Out = M[lift.Out]

			def apply(ma: M[A], mb: M[B], mc: M[C], f: Fn) = ap.ap(ma){
				ap.ap(mb){
					ap.map(mc){ c: C => b: B => a: A => lift(a, b, c, f) }
				}
			}
		}
}