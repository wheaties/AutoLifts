package autolift.cats

import cats.{Functor, Foldable, Order}
import autolift.{LiftMinimum, LiftMinimumSyntax}

trait CatsLiftMinimum[Obj, A] extends LiftMinimum[Obj, A] with Serializable

object CatsLiftMinimum extends LowPriorityCatsLiftMinimum{
  def apply[Obj, A](implicit lift: CatsLiftMinimum[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A](implicit fold: Foldable[F], ord: Order[A]): Aux[F[A], A, Option[A]] =
    new CatsLiftMinimum[F[A], A]{
      type Out = Option[A]

      def apply(fa: F[A]) = fold.minimumOption(fa)
    }
}

trait LowPriorityCatsLiftMinimum{
  type Aux[Obj, A, Out0] = CatsLiftMinimum[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, A](implicit functor: Functor[F], lift: LiftMinimum[G, A]): Aux[F[G], A, F[lift.Out]] =
    new CatsLiftMinimum[F[G], A]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LiftMinimumExport{
  implicit def mkMin[Obj, A](implicit lift: CatsLiftMinimum[Obj, A]): CatsLiftMinimum.Aux[Obj, A, lift.Out] = lift
}

trait LiftMinimumPackage extends LiftMinimumExport
  with LiftMinimumSyntax