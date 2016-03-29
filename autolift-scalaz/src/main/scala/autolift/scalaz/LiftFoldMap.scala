package autolift.scalaz

import scalaz.{Foldable, Functor, Monoid, Unapply}
import autolift.{LiftFoldMap, LiftFoldMapSyntax}

trait ScalazLiftFoldMap[FA, Fn] extends LiftFoldMap[FA, Fn]

object ScalazLiftFoldMap extends LowPriorityScalazLiftFoldMap{
	def apply[FA, Fn](implicit lift: ScalazLiftFoldMap[FA, Fn]): Aux[FA, Fn, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new ScalazLiftFoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityScalazLiftFoldMap extends LowPriorityScalazLiftFoldMap1{
	implicit def unbase[FA, A, C >: A, B](implicit un: Un.Apply[Foldable, FA, A], ev: Monoid[B]): Aux[FA, C => B, B] =
		new ScalazLiftFoldMap[FA, C => B]{
			type Out = B

			def apply(fa: FA, f: C => B) = un.TC.foldMap(un(fa))(f)
		}
}

trait LowPriorityScalazLiftFoldMap1 extends LowPriorityScalazLiftFoldMap2{
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftFoldMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftFoldMap[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait LowPriorityScalazLiftFoldMap2{
	type Aux[FA, Fn, Out0] = ScalazLiftFoldMap[FA, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftFoldMap[G, Fn]): Aux[FG, Fn, un.M[lift.Out]] =
		new ScalazLiftFoldMap[FG, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, f) }
		}
}

trait ScalazLiftFoldMapSyntax extends LiftFoldMapSyntax with LowPriorityLiftFoldMapSyntax

trait LowPriorityLiftFoldMapSyntax{

  /// Syntax extension providing for a `liftFoldMap` method.
  implicit class LowLiftFoldMapOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){
    /**
     * Automatic lifting of a Fold dictated by the signature of a function and given that the mapping maps one type 
     * to another which has a Monoid.
     *
     * @param f the function over which to fold.
     * @tparam B the argument of the function.
     * @tparam C the return type of the function which must have a Monoid.
     */
    def liftFoldMap[B, C](f: B => C)(implicit lift: LiftFoldMap[FA, B => C]): lift.Out = lift(fa, f)
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

