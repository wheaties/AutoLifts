package autolift.cats

import cats.{Functor, Monoid, Foldable, Unapply}
import autolift.{LiftFold, LiftFoldSyntax}

trait CatsLiftFold[F[_], Obj] extends LiftFold[F, Obj]

object CatsLiftFold extends LowPriorityCatsLiftFold{
  def apply[F[_], Obj](implicit fold: CatsLiftFold[F, Obj]): Aux[F, Obj, fold.Out] = fold

  implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
    new CatsLiftFold[F, F[A]]{
      type Out = A

      def apply(fa: F[A]) = fold.fold(fa)
    }
}

trait LowPriorityCatsLiftFold extends LowPriorityCatsLiftFold1{
  implicit def unbase[FA, A](implicit un: Un.Apply[Foldable, FA, A], m: Monoid[A]): Aux[un.M, FA, A] =
    new CatsLiftFold[un.M, FA]{
      type Out = A

      def apply(fa: FA) = un.TC.fold(un.subst(fa))
    }
}

trait LowPriorityCatsLiftFold1 extends LowPriorityCatsLiftFold2{
  implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFold[F, H]): Aux[F, G[H], G[fold.Out]] =
    new CatsLiftFold[F, G[H]]{
      type Out = G[fold.Out]

      def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
    }
}

trait LowPriorityCatsLiftFold2{
  type Aux[F[_], Obj, Out0] = CatsLiftFold[F, Obj]{ type Out = Out0 }

  implicit def unrecur[F[_], GH, H](implicit un: Un.Apply[Functor, GH, H], fold: LiftFold[F, H]): Aux[F, GH, un.M[fold.Out]] =
    new CatsLiftFold[F, GH]{
      type Out = un.M[fold.Out]

      def apply(gh: GH) = un.TC.map(un.subst(gh)){ h: H => fold(h) }
    }
}

trait CatsLiftFoldSyntax extends LiftFoldSyntax with LowPriorityLiftFoldSyntax

trait LowPriorityLiftFoldSyntax{

  /// Syntax extension providing for a `liftFold` method.
  implicit class LowLiftFoldOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a Fold at the indicated type, assuming the type permits folding and the contained type has 
     * a Monoid.
     * 
     * @tparam M the higher-kinded type for which there is the notion of folding or traversing.
     */
    def liftFold[M[_]](implicit fold: LiftFold[M, FA]): fold.Out = fold(fa)
  }
}