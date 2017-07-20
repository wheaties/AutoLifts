package autolift.cats

import cats.{Functor, Foldable, Order}
import autolift.{LiftMaximum, LiftMaximumSyntax}

trait CatsLiftMaximum[Obj, A] extends LiftMaximum[Obj, A] with Serializable

object CatsLiftMaximum extends LowPriorityCatsLiftMaximum{
  def apply[Obj, A](implicit lift: CatsLiftMaximum[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A](implicit fold: Foldable[F], ord: Order[A]): Aux[F[A], A, Option[A]] =
    new CatsLiftMaximum[F[A], A]{
      type Out = Option[A]

      def apply(fa: F[A]) = fold.maximumOption(fa)
    }
}

trait LowPriorityCatsLiftMaximum{
  type Aux[Obj, A, Out0] = CatsLiftMaximum[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, A](implicit functor: Functor[F], lift: LiftMaximum[G, A]): Aux[F[G], A, F[lift.Out]] =
    new CatsLiftMaximum[F[G], A]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LiftMaximumPackage extends LiftMaximumSyntax{
  implicit def mkMax[Obj, A](implicit lift: CatsLiftMaximum[Obj, A]): CatsLiftMaximum.Aux[Obj, A, lift.Out] = lift
}