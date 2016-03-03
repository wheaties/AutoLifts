package autolift.cats

import cats.{Functor, Foldable}
import autolift.{LiftExists, LiftedExists}

trait CatsLiftExists[Obj, Fn] extends LiftExists[Obj, Fn]

object CatsLiftExists extends LowPriorityCatsLiftExists {
  def apply[Obj, Fn](implicit lift: CatsLiftExists[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]): Aux[F[A], C => Boolean, Boolean] =
    new CatsLiftExists[F[A], C => Boolean]{
      type Out = Boolean

      def apply(fa: F[A], f: C => Boolean) = fold.exists(fa)(f)
    }
}

trait LowPriorityCatsLiftExists extends LowPriorityCatsLiftExists1{
  implicit def unbase[FA, A, C >: A](implicit unapply: Un.Apply[Foldable, FA, A]): Aux[FA, C => Boolean, Boolean] =
    new CatsLiftExists[FA, C => Boolean]{
      type Out = Boolean

      def apply(fa: FA, f: C => Boolean) = unapply.TC.exists(unapply.subst(fa))(f)
    }
}

trait LowPriorityCatsLiftExists1 extends LowPriorityCatsLiftExists2{
  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftExists[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftExists[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait LowPriorityCatsLiftExists2{
  type Aux[Obj, Fn, Out0] = CatsLiftExists[Obj, Fn]{ type Out = Out0 }

  implicit def unrecur[FG, F[_], G, Fn](implicit unapply: Un.Aux[Functor, FG, F, G], lift: LiftExists[G, Fn]): Aux[FG, Fn, F[lift.Out]] =
    new CatsLiftExists[FG, Fn]{
      type Out = F[lift.Out]

      def apply(fg: FG, f: Fn) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, f) }
    }
}

