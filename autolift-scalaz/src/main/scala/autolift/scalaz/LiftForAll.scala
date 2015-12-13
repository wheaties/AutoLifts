package autolift.scalaz

import scalaz.{Functor, Foldable}
import autolift.{LiftForAll, LiftedForAll}
import export._

trait ScalazLiftForAll[Obj, Fn] extends LiftForAll[Obj, Fn]

@exports(Subclass)
object ScalazLiftForAll extends LowPriorityScalazLiftForAll {
	def apply[Obj, Fn](implicit lift: ScalazLiftForAll[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]): Aux[F[A], C => Boolean, Boolean] =
		new ScalazLiftForAll[F[A], C => Boolean]{
			type Out = Boolean

			def apply(fa: F[A], f: C => Boolean) = fold.all(fa)(f)
		}
}

trait LowPriorityScalazLiftForAll{
	type Aux[Obj, Fn, Out0] = ScalazLiftForAll[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftForAll[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftForAll[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LiftAllSyntax{
	implicit class LiftAllOps[F[_], A](fa: F[A]){
		def liftAll[B](f: B => Boolean)(implicit lift: LiftForAll[F[A], B => Boolean]): lift.Out = lift(fa, f)
	}
}

trait LiftAllContext{
	def liftAll[A](f: A => Boolean): LiftedAll[A] = new LiftedForAll(f)

	type LiftedAll[A] = LiftedForAll[A]
}