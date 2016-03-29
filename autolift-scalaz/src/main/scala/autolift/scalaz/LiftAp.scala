package autolift.scalaz

import scalaz.{Functor, Apply, Unapply}
import autolift.{LiftAp, LiftApSyntax}

trait ScalazLiftAp[Obj, Fn] extends LiftAp[Obj, Fn]

object ScalazLiftAp extends LowPriorityScalazLiftAp {
	def apply[Obj, Fn](implicit lift: ScalazLiftAp[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new ScalazLiftAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}

trait LowPriorityScalazLiftAp extends LowPriorityScalazLiftAp1{
	implicit def unbase[FA, FAB, A, B](implicit un: Un.Apply2[Apply, FA, FAB, A, A => B]): Aux[FA, FAB, un.M[B]] =
		new ScalazLiftAp[FA, FAB]{
			type Out = un.M[B]

			def apply(fa: FA, fab: FAB) = un.TC.ap(un._1(fa))(un._2(fab))
		}
}

trait LowPriorityScalazLiftAp1 extends LowPriorityScalazLiftAp2{
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftAp[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftAp[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LowPriorityScalazLiftAp2{
	type Aux[Obj, Fn, Out0] = ScalazLiftAp[Obj, Fn]{ type Out = Out0 }

	implicit def urecur[FG, G, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftAp[G, Fn]): Aux[FG, Fn, un.M[lift.Out]] =
		new ScalazLiftAp[FG, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, f) }
		}
}

trait ScalazLiftApSyntax extends LiftApSyntax with LowPriorityLiftApSyntax

trait LowPriorityLiftApSyntax{

	/// Syntax extension providing for a `liftAp` method.
	implicit class LowLiftApOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

		/**
		 * Automatic Applicative lifting of the contained function `f` such that the application point is dictated by the
		 * type of the Applicative.
		 *
		 * @param f the wrapped function to be lifted.
		 * @tparam MBC the argument type of the wrapped function.
		 */
		def liftAp[MBC](f: MBC)(implicit lift: LiftAp[FA, MBC]): lift.Out = lift(fa, f)
	}
}

final class LiftedAp[A, B, F[_]](protected val f: F[A => B])(implicit ap: Apply[F]){
	def andThen[C >: B, D](lf: LiftedAp[C, D, F]) = new LiftedAp(ap.ap(f)(
		ap.map(lf.f){ 
			y: (C => D) => { x: (A => B) => x andThen y } 
		}
	))

	def compose[C, D <: A](lf: LiftedAp[C, D, F]) = lf andThen this

	def map[C](g: B => C): LiftedAp[A, C, F] = new LiftedAp(ap.map(f){ _ andThen g })

	def apply[That](that: That)(implicit lift: LiftAp[That, F[A => B]]): lift.Out = lift(that, f)
}

trait LiftedApImplicits{
	implicit def liftedApFunctor[A, F[_]] = new Functor[LiftedAp[A, ?, F]]{
		def map[B, C](lap: LiftedAp[A, B, F])(f: B => C) = lap map f
	}
}

trait LiftApContext{
	def liftAp[A, B, F[_]](f: F[A => B])(implicit ap: Apply[F]) = new LiftedAp(f)

	def liftAp[A, B, FAB](f: FAB)(implicit un: Un.Apply[Apply, FAB, A => B]) = new LiftedAp(un(f))(un.TC)
}

