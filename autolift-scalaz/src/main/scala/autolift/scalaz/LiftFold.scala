package autolift.scalaz

import scalaz.{Functor, Foldable, Monoid, Unapply}
import autolift.{LiftFold, LiftFoldSyntax}

trait ScalazLiftFold[Obj] extends LiftFold[Obj]

object ScalazLiftFold extends LowPriorityScalazLiftFold{
	def apply[FA](implicit lift: ScalazLiftFold[FA]): Aux[FA, lift.Out] = lift

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new ScalazLiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazLiftFold extends LowPriorityScalazLiftFold1{
	implicit def unbase[FA, A](implicit un: Un.Apply[Foldable, FA, A], ev: Monoid[A]): Aux[FA, A] =
		new ScalazLiftFold[FA]{
			type Out = un.A

			def apply(fa: FA) = un.TC.fold(un(fa))
		}
}

trait LowPriorityScalazLiftFold1 extends LowPriorityScalazLiftFold2{
	implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
		new ScalazLiftFold[F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

trait LowPriorityScalazLiftFold2{
	type Aux[FA, Out0] = ScalazLiftFold[FA]{ type Out = Out0 }

	implicit def unrecur[FG, G](implicit un: Un.Apply[Functor, FG, G], lift: LiftFold[G]): Aux[FG, un.M[lift.Out]] =
		new ScalazLiftFold[FG]{
			type Out = un.M[lift.Out]

			def apply(fg: FG) = un.TC.map(un(fg)){ g: G => lift(g) }
		}
}

trait ScalazLiftFoldSyntax extends LiftFoldSyntax with LowPriorityLiftFoldSyntax

trait LowPriorityLiftFoldSyntax{

  /// Syntax extension providing for a `liftFold` method.
  implicit class LowLiftFoldOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a Fold on the first nested type which has as a type parameter a Monoid.
     */
    def liftFold(implicit lift: LiftFold[FA]): lift.Out = lift(fa)
  }
}