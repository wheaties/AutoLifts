package autolift.scalaz

import scalaz.{Functor, Foldable, Monoid, Unapply}
import autolift.{LiftFoldAt, LiftFoldAtSyntax}

trait ScalazLiftFoldAt[F[_], Obj] extends LiftFoldAt[F, Obj]

object ScalazLiftFoldAt extends LowPriorityScalazLiftFoldAt{
	def apply[F[_], Obj](implicit fold: ScalazLiftFoldAt[F, Obj]): Aux[F, Obj, fold.Out] = fold

	implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
		new ScalazLiftFoldAt[F, F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazLiftFoldAt extends LowPriorityScalazLiftFoldAt1{
	implicit def unbase[FA, A](implicit un: Un.Apply[Foldable, FA, A], m: Monoid[A]): Aux[un.M, FA, A] =
		new ScalazLiftFoldAt[un.M, FA]{
			type Out = A

			def apply(fa: FA) = un.TC.fold(un(fa))
		}
}

trait LowPriorityScalazLiftFoldAt1 extends LowPriorityScalazLiftFoldAt2{
	implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFoldAt[F, H]): Aux[F, G[H], G[fold.Out]] =
		new ScalazLiftFoldAt[F, G[H]]{
			type Out = G[fold.Out]

			def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
		}
}

trait LowPriorityScalazLiftFoldAt2{
	type Aux[F[_], Obj, Out0] = ScalazLiftFoldAt[F, Obj]{ type Out = Out0 }

	implicit def unrecur[F[_], GH, H](implicit un: Un.Apply[Functor, GH, H], fold: LiftFoldAt[F, H]): Aux[F, GH, un.M[fold.Out]] =
		new ScalazLiftFoldAt[F, GH]{
			type Out = un.M[fold.Out]

			def apply(gh: GH) = un.TC.map(un(gh)){ h: H => fold(h) }
		}
}

trait ScalazLiftFoldAtSyntax extends LiftFoldAtSyntax with LowPriorityLiftFoldAtSyntax

trait LowPriorityLiftFoldAtSyntax{

  /// Syntax extension providing for a `liftFoldAt` method.
  implicit class LowLiftFoldAtOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a Fold at the indicated type, assuming the type permits folding and the contained type has 
     * a Monoid.
     * 
     * @tparam M the higher-kinded type for which there is the notion of folding or traversing.
     */
    def liftFoldAt[M[_]](implicit fold: LiftFoldAt[M, FA]): fold.Out = fold(fa)
  }
}