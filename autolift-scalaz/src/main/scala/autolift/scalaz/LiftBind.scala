package autolift.scalaz

import scalaz.{Functor, Bind, Unapply}
import autolift.LiftFlatMap


trait ScalazLiftFlatMap[Obj, Fn] extends LiftFlatMap[Obj, Fn]

object ScalazLiftFlatMap extends LowPriorityScalazLiftFlatMap {
	def apply[Obj, Fn](implicit lift: ScalazLiftFlatMap[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	implicit def base[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M[A], C => M[B], M[B]] =
		new ScalazLiftFlatMap[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = bind.bind(fa)(f)
		}
}

trait LowPriorityScalazLiftFlatMap extends LowPriorityScalazLiftFlatMap1{
	implicit def unbase[MA, A, C >: A, MB, B](implicit un: Un.Apply2[Bind, MA, MB, A, B]): Aux[MA, C => MB, un.M[un.B]] =
		new ScalazLiftFlatMap[MA, C => MB]{
			type Out = un.M[un.B]

			def apply(ma: MA, f: C => MB) = un.TC.bind(un._1(ma)){ a: A => un._2(f(a)) }
		}
}

trait LowPriorityScalazLiftFlatMap1 extends LowPriorityScalazLiftFlatMap2{
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftFlatMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftFlatMap[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LowPriorityScalazLiftFlatMap2{
	type Aux[Obj, Fn, Out0] = ScalazLiftFlatMap[Obj, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftFlatMap[G, Fn]): Aux[FG, Fn, un.M[lift.Out]] =
		new ScalazLiftFlatMap[FG, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, f) }
		}
}

trait LiftBindSyntax extends LowPriorityLiftBindSyntax{

	/// Syntax extension providing for a `LiftBind` method.
	implicit class LiftBindOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting and flattening of the contained function `f` such that the application point is dicated by the
		 * argument and return type of the function.
		 *
		 * @param f the function that returns a type with a Bind.
		 * @tparam B the argument type of the function.
		 * @tparam MC the type of the return type of the function for which there exists an `Unapply` on a `Bind`.
		 */
		def liftBind[B, MC](f: B => MC)(implicit lift: LiftFlatMap[F[A], B => MC]): lift.Out = lift(fa, f)
	}
}

trait LowPriorityLiftBindSyntax{

	/// Syntax extension providing for a `LiftBind` method.
	implicit class LowLiftBindOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

		/**
		 * Automatic lifting and flattening of the contained function `f` such that the application point is dicated by the
		 * argument and return type of the function.
		 *
		 * @param f the function that returns a type with a Bind.
		 * @tparam B the argument type of the function.
		 * @tparam MC the type of the return type of the function for which there exists an `Unapply` on a `Bind`.
		 */
		def liftBind[B, MC](f: B => MC)(implicit lift: LiftFlatMap[FA, B => MC]): lift.Out = lift(fa, f)
	}
}

final class LiftedBind[A, B, M[_]](protected val f: A => M[B])(implicit bind: Bind[M]){
	def andThen[C >: B, D](that: LiftedBind[C, D, M]) = new LiftedBind({ x: A => bind.bind(f(x))(that.f) })

	def compose[C, D <: A](that: LiftedBind[C, D, M]) = that andThen this

	def map[C](g: B => C): LiftedBind[A, C, M] = new LiftedBind({ x: A => bind.map(f(x))(g) })

	def apply[That](that: That)(implicit lift: LiftFlatMap[That, A => M[B]]): lift.Out = lift(that, f)
}

trait LiftedBindImplicits{
	implicit def liftedBindFunctor[A, M[_]] = new Functor[LiftedBind[A, ?, M]]{
		def map[B, C](lb: LiftedBind[A, B, M])(f: B => C) = lb map f
	}
}

trait LiftBindContext{
	def liftBind[A, B, M[_]](f: A => M[B])(implicit bind: Bind[M]) = new LiftedBind(f)
}

