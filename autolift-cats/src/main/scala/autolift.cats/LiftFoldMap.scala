package autolift.cats

import autolift.LiftFoldMap
import export._

trait ScalazLiftFoldMap[FA, Fn] extends LiftFoldMap[FA, Fn]

@exports(Subclass)
object ScalazLiftFoldMap extends LowPriorityScalazLiftFoldMap{
	def apply[FA, Fn](implicit lift: ScalazLiftFoldMap[FA, Fn]): Aux[FA, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new ScalazLiftFoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityScalazLiftFoldMap{
	type Aux[FA, Fn, Out0] = ScalazLiftFoldMap[FA, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftFoldMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftFoldMap[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

final class LiftedFoldMap[A, B : Monoid](f: A => B){
	def andThen[C >: B, D : Monoid](that: LiftedFoldMap[C, D]) = that compose this

	def compose[C, D <: A](that: LiftedFoldMap[C, D]) = that map f

	def map[C : Monoid](g: B => C): LiftedFoldMap[A, C] = new LiftedFoldMap(f andThen g)

	def apply[That](that: That)(implicit lift: LiftFoldMap[That, A => B]): lift.Out = lift(that, f)
}

trait LiftFoldMapContext{
	def liftFoldMap[A, B : Monoid](f: A => B) = new LiftedFoldMap(f)
}