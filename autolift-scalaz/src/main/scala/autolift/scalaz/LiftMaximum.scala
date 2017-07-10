package autolift.scalaz

import scalaz.{Functor, Foldable, Order}
import autolift.{LiftMaximum, LiftMaximumSyntax}

trait ScalazLiftMaximum[Obj, A] extends LiftMaximum[Obj, A]

object ScalazLiftMaximum extends LowPriorityScalazLiftMaximum{
  def apply[Obj, A](implicit lift: ScalazLiftMaximum[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A](implicit fold: Foldable[F], ord: Order[A]): Aux[F[A], A, Option[A]] =
    new ScalazLiftMaximum[F[A], A]{
      type Out = Option[A]

      def apply(fa: F[A]) = fold.maximum(fa)
    }
}

trait LowPriorityScalazLiftMaximum{
  type Aux[Obj, A, Out0] = ScalazLiftMaximum[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, A](implicit functor: Functor[F], lift: LiftMaximum[G, A]): Aux[F[G], A, F[lift.Out]] =
    new ScalazLiftMaximum[F[G], A]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LiftMaximumExport{
  implicit def mkMin[Obj, A](implicit lift: ScalazLiftMaximum[Obj, A]): ScalazLiftMaximum.Aux[Obj, A, lift.Out] = lift
}

trait LiftMaximumPackage extends LiftMaximumExport
  with LiftMaximumSyntax