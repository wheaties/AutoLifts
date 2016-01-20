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

trait LowPriorityCatsLiftExists{
  type Aux[Obj, Fn, Out0] = CatsLiftExists[Obj, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftExists[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftExists[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

