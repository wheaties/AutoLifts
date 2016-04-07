package autolift.scalaz

import scalaz.{Functor, Foldable, Unapply}
import autolift.{LiftForAll, LiftedForAll}

trait ScalazLiftForAll[Obj, Fn] extends LiftForAll[Obj, Fn]

object ScalazLiftForAll extends LowPriorityScalazLiftForAll {
	def apply[Obj, Fn](implicit lift: ScalazLiftForAll[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]): Aux[F[A], C => Boolean, Boolean] =
		new ScalazLiftForAll[F[A], C => Boolean]{
			type Out = Boolean

			def apply(fa: F[A], f: C => Boolean) = fold.all(fa)(f)
		}
}

trait LowPriorityScalazLiftForAll extends LowPriorityScalazLiftForAll1{
	implicit def unbase[FA, A, C >: A](implicit un: Un.Apply[Foldable, FA, A]): Aux[FA, C => Boolean, Boolean] =
		new ScalazLiftForAll[FA, C => Boolean]{
			type Out = Boolean

			def apply(fa: FA, f: C => Boolean) = un.TC.all(un(fa))(f)
		}
}

trait LowPriorityScalazLiftForAll1 extends LowPriorityScalazLiftForAll2{
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftForAll[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftForAll[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LowPriorityScalazLiftForAll2{
	type Aux[Obj, Fn, Out0] = ScalazLiftForAll[Obj, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftForAll[G, Fn]): Aux[FG, Fn, un.M[lift.Out]] =
		new ScalazLiftForAll[FG, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, f) }
		}
}

trait LiftAllSyntax extends LowPriorityLiftAllSyntax{
	implicit class LiftAllOps[F[_], A](fa: F[A]){
		def liftAll[B](f: B => Boolean)(implicit lift: LiftForAll[F[A], B => Boolean]): lift.Out = lift(fa, f)
	}
}

trait LowPriorityLiftAllSyntax{
	implicit class LiftAllOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){
		def liftAll[B](f: B => Boolean)(implicit lift: LiftForAll[FA, B => Boolean]): lift.Out = lift(fa, f)
	}
}

trait LiftAllContext{
	def liftAll[A](f: A => Boolean): LiftedAll[A] = new LiftedForAll(f)

	type LiftedAll[A] = LiftedForAll[A]
}

