package autolift.scalaz

import scalaz.{Functor, Unapply}
import autolift.{LiftMap, LiftedMap, LiftMapSyntax}

trait ScalazLiftMap[Obj, Fn] extends LiftMap[Obj, Fn]

object ScalazLiftMap extends LowPriorityScalazLiftMap {
	def apply[Obj, Fn](implicit lift: ScalazLiftMap[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new ScalazLiftMap[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityScalazLiftMap extends LowPriorityScalazLiftMap1{
	implicit def unbase[FA, A, C >: A, B](implicit un: Un.Apply[Functor, FA, A]): Aux[FA, C => B, un.M[B]] =
		new ScalazLiftMap[FA, C => B]{
			type Out = un.M[B]

			def apply(fa: FA, f: C => B) = un.TC.map(un(fa))(f)
		}
}

trait LowPriorityScalazLiftMap1 extends LowPriorityScalazLiftMap2{
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftMap[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LowPriorityScalazLiftMap2{
	type Aux[Obj, Fn, Out0] = ScalazLiftMap[Obj, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftMap[G, Fn]): Aux[FG, Fn, un.M[lift.Out]] =
		new ScalazLiftMap[FG, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, f) }
		}
}

trait LiftedMapImplicits{
	implicit def liftedMapFunctor[A] = new Functor[LiftedMap[A, ?]]{
		def map[B, C](lm: LiftedMap[A, B])(f: B => C) = lm map f
	}
}

trait ScalazLiftMapSyntax extends LiftMapSyntax with LowPriorityLiftMapSyntax

trait LowPriorityLiftMapSyntax{

  /// Syntax extension providing for a `liftMap` method on an object with shape differing from F[A].
  implicit class LowLiftMapOps[MA](ma: MA)(implicit ev: Unapply[Functor, MA]){

    /**
     * Automatic lifting of the function `f` over the object such that the application point is dictated by the type
     * of function invocation.
     *
     * @param f the function to be lifted.
     * @tparam B the argument type of the function.
     * @tparam C the return type of the function.
     */
    def liftMap[B, C](f: B => C)(implicit lift: LiftMap[MA, B => C]): lift.Out = lift(ma, f)
  }
}