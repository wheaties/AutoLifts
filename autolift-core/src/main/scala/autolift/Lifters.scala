package autolift

import scalaz.{Functor, Apply, Applicative, Bind, Foldable, Monoid, MonadPlus}
import export._

trait LiftImplicits{
	/** Implicit exposing methods on any type constructor which provides automatic function lifting based upon the 
	 *  type of the function.
	 *
	 * @param fa An instance of `F[A]`.
	 * @tparam F A type constructor
	 * @tparam A The type within `F`.
	 */
	implicit class LifterOps[F[_], A](fa: F[A]){

		def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z)(implicit lift: LiftFoldRight[F[A], (B, => Z) => Z, Z]): lift.Out = 
			lift(fa, f, z)
	}
}

//TODO: split into another file, a la ops, syntax.
trait LiftFunctions{ //These are autolifting contexts

	def liftAp[A, B, F[_]](f: F[A => B])(implicit ap: Apply[F]) = new LiftedAp(f)

	sealed class LiftedAp[A, B, F[_]](protected val f: F[A => B])(implicit ap: Apply[F]){
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

	sealed class LiftedFlatMap[A, B, M[_]](protected val f: A => M[B])(implicit bind: Bind[M]){
		def andThen[C >: B, D](that: LiftedFlatMap[C, D, M]) = new LiftedFlatMap({ x: A => bind.bind(f(x))(that.f) })

		def compose[C, D <: A](that: LiftedFlatMap[C, D, M]) = that andThen this

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
/*trait LiftM2[Obj1, Obj2, Function] extends DFunction3[Obj1, Obj2, Function]

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
}*/

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
/*trait LiftM3[Obj1, Obj2, Obj3, Function] extends DFunction4[Obj1, Obj2, Obj3, Function]

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
}*/

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