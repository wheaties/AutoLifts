package autolift.cats

import cats.{Functor, Monoid, Foldable}
import autolift.LiftFold


trait CatsLiftFold[Obj] extends LiftFold[Obj]

object CatsLiftFold extends LowPriorityCatsLiftFold{
  def apply[FA](implicit lift: CatsLiftFold[FA]): Aux[FA, lift.Out] = lift

  implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
    new CatsLiftFold[F[A]]{
      type Out = A

      def apply(fa: F[A]) = fold.fold(fa)
    }
}

trait LowPriorityCatsLiftFold extends LowPriorityCatsLiftFold1{
  implicit def unbase[FA, A](implicit unapply: Un.Apply[Foldable, FA, A], ev: Monoid[A]): Aux[FA, A] =
    new CatsLiftFold[FA]{
      type Out = A

      def apply(fa: FA) = unapply.TC.fold(unapply.subst(fa))
    }
}

trait LowPriorityCatsLiftFold1 extends LowPriorityCatsLiftFold2{
  implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
    new CatsLiftFold[F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LowPriorityCatsLiftFold2{
  type Aux[FA, Out0] = CatsLiftFold[FA]{ type Out = Out0 }

  implicit def unrecur[FG, G](implicit unapply: Un.Apply[Functor, FG, G], lift: LiftFold[G]): Aux[FG, unapply.M[lift.Out]] =
    new CatsLiftFold[FG]{
      type Out = unapply.M[lift.Out]

      def apply(fg: FG) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g) }
    }
}

