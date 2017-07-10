package autolift.scalaz

import scalaz.{Functor, Foldable, Order}
import autolift.{LiftMinimum, LiftMinimumSyntax}

trait ScalazLiftMinimum[Obj, A] extends LiftMinimum[Obj, A]

object ScalazLiftMinimum extends LowPriorityScalazLiftMinimum{
  def apply[Obj, A](implicit lift: ScalazLiftMinimum[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A](implicit fold: Foldable[F], ord: Order[A]): Aux[F[A], A, Option[A]] =
    new ScalazLiftMinimum[F[A], A]{
      type Out = Option[A]

      def apply(fa: F[A]) = fold.minimum(fa)
    }
}

trait LowPriorityScalazLiftMinimum{
  type Aux[Obj, A, Out0] = ScalazLiftMinimum[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, A](implicit functor: Functor[F], lift: LiftMinimum[G, A]): Aux[F[G], A, F[lift.Out]] =
    new ScalazLiftMinimum[F[G], A]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LiftMinimumExport{
  implicit def mkMin[Obj, A](implicit lift: ScalazLiftMinimum[Obj, A]): ScalazLiftMinimum.Aux[Obj, A, lift.Out] = lift
}

trait LiftMinimumPackage extends LiftMinimumExport
  with LiftMinimumSyntax