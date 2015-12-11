package autolift.scalaz

import scalaz.{Functor, Bind}
import autolift.LiftB
import export._


trait ScalazLiftB[Obj, Fn] extends LiftB[Obj, Fn]

@exports(Subclass)
object ScalazLiftB extends LowPriorityScalazLiftB {
	def apply[Obj, Fn](implicit lift: ScalazLiftB[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M[A], C => M[B], M[B]] =
		new ScalazLiftB[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = bind.bind(fa)(f)
		}
}

trait LowPriorityScalazLiftB{
	type Aux[Obj, Fn, Out0] = ScalazLiftB[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftB[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftB[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LiftBindSyntax{
	/// Syntax extension providing for a `liftBind` method.
	implicit class LiftBindOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting and flattening of the contained function `f` such that the application point is dicated by the
		 * argument and return type of the function.
		 *
		 * @param f the function that returns a type with a Bind.
		 * @tparam B the argument type of the function.
		 * @tparam C the inner type of the return type of the function.
		 * @tparam M the higher-kinded type of the return type of the function which has a Bind.
		 */
		def liftBind[B, C, M[_]](f: B => M[C])(implicit lift: LiftB[F[A], B => M[C]]): lift.Out = lift(fa, f)
	}
}

final class LiftedBind[A, B, M[_]](protected val f: A => M[B])(implicit bind: Bind[M]){
	def andThen[C >: B, D](that: LiftedBind[C, D, M]) = new LiftedBind({ x: A => bind.bind(f(x))(that.f) })

	def compose[C, D <: A](that: LiftedBind[C, D, M]) = that andThen this

	def map[C](g: B => C): LiftedBind[A, C, M] = new LiftedBind({ x: A => bind.map(f(x))(g) })

	def apply[That](that: That)(implicit lift: LiftB[That, A => M[B]]): lift.Out = lift(that, f)
}

trait LiftBindContext{
	def liftFlatMap[A, B, M[_]](f: A => M[B])(implicit bind: Bind[M]) = new LiftedBind(f)

	def liftBind[A, B, M[_]](f: A => M[B])(implicit bind: Bind[M]) = new LiftedBind(f)
}