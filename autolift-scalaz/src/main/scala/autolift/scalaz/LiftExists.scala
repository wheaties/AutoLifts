package autolift.scalaz

import scalaz.{Functor, Foldable, Unapply}
import autolift.{LiftExists, LiftedExists}

trait ScalazLiftExists[Obj, Fn] extends LiftExists[Obj, Fn]

object ScalazLiftExists extends LowPriorityScalazLiftExists {
	def apply[Obj, Fn](implicit lift: ScalazLiftExists[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]): Aux[F[A], C => Boolean, Boolean] =
		new ScalazLiftExists[F[A], C => Boolean]{
			type Out = Boolean

			def apply(fa: F[A], f: C => Boolean) = fold.any(fa)(f)
		}
}

trait LowPriorityScalazLiftExists extends LowPriorityScalazLiftExists1{
	implicit def unbase[FA, A, C >: A](implicit un: Un.Apply[Foldable, FA, A]): Aux[FA, C => Boolean, Boolean] =
		new ScalazLiftExists[FA, C => Boolean]{
			type Out = Boolean

			def apply(fa: FA, f: C => Boolean) = un.TC.any(un(fa))(f)
		}
}

trait LowPriorityScalazLiftExists1 extends LowPriorityScalazLiftExists2{
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftExists[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftExists[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LowPriorityScalazLiftExists2{
	type Aux[Obj, Fn, Out0] = ScalazLiftExists[Obj, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftExists[G, Fn]): Aux[FG, Fn, un.M[lift.Out]] =
		new ScalazLiftExists[FG, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, f) }
		}
}

trait LiftAnySyntax extends LowPriorityLiftAnySyntax{
	implicit class LiftAnyOps[F[_], A](fa: F[A]){
		def liftAny[B](f: B => Boolean)(implicit lift: LiftExists[F[A], B => Boolean]): lift.Out = lift(fa, f)
	}
}

trait LowPriorityLiftAnySyntax{
	implicit class LowLiftAnyOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){
		def liftAny[B](f: B => Boolean)(implicit lift: LiftExists[FA, B => Boolean]): lift.Out = lift(fa, f)
	}
}

trait LiftAnyContext{
	def liftAny[A](f: A => Boolean): LiftedAny[A] = new LiftedExists(f)

	type LiftedAny[A] = LiftedExists[A]
}
