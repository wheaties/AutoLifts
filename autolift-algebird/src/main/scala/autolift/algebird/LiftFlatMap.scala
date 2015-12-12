package autolift.algebird

import autolift.LiftB
import com.twitter.algebird.{Functor, Monad}
import export._

trait AlgeLiftB[Obj, Fn] extends LiftB[Obj, Fn]

@exports(Subclass)
object AlgeLiftB extends LowPriorityAlgeLiftB {
	def apply[Obj, Fn](implicit lift: AlgeLiftB[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A, C >: A, B](implicit fm: Monad[M]): Aux[M[A], C => M[B], M[B]] =
		new AlgeLiftB[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = fm.flatMap(fa)(f)
		}
}

trait LowPriorityAlgeLiftB{
	type Aux[Obj, Fn, Out0] = AlgeLiftB[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftB[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new AlgeLiftB[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

final class LiftedFlatMap[A, B, M[_]](protected val f: A => M[B])(implicit fm: Monad[M]){
	def andThen[C >: B, D](that: LiftedFlatMap[C, D, M]) = new LiftedFlatMap({ x: A => fm.flatMap(f(x))(that.f) })

	def compose[C, D <: A](that: LiftedFlatMap[C, D, M]) = that andThen this

	def map[C](g: B => C): LiftedFlatMap[A, C, M] = new LiftedFlatMap({ x: A => fm.map(f(x))(g) })

	def apply[That](that: That)(implicit lift: LiftB[That, A => M[B]]): lift.Out = lift(that, f)
}

trait LiftFlatMapContext{
	def liftFlatMap[A, B, M[_]](f: A => M[B])(implicit fm: Monad[M]) = new LiftedFlatMap(f)
}