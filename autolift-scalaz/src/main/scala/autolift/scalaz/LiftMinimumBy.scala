package autolift.scalaz

import scalaz.{Functor, Foldable, Order}
import autolift.{LiftMinimumBy, LiftMinimumBySyntax}

trait ScalazLiftMinimumBy[Obj, Fn] extends LiftMinimumBy[Obj, Fn]

object ScalazLiftMinimumBy extends LowPriorityScalazLiftMinimumBy{
  def apply[Obj, A](implicit lift: ScalazLiftMinimumBy[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A, B, C >: A](implicit fold: Foldable[F], ord: Order[B]): Aux[F[A], C => B, Option[A]] =
    new ScalazLiftMinimumBy[F[A], C => B]{
      type Out = Option[A]

      def apply(fa: F[A], fn: C => B) = fold.minimumBy(fa)(fn)
    }
}

trait LowPriorityScalazLiftMinimumBy{
  type Aux[Obj, A, Out0] = ScalazLiftMinimumBy[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMinimumBy[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new ScalazLiftMinimumBy[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], fn: Fn) = functor.map(fg){ g: G => lift(g, fn) }
    }
}

final class LiftedMinimumBy[A, B : Order](f: A => B){
  def map[C : Order](g: B => C): LiftedMinimumBy[A, C] = new LiftedMinimumBy(f andThen g)

  def apply[That](that: That)(implicit lift: LiftMinimumBy[That, A => B]): lift.Out = lift(that, f)
}

trait LiftMinimumByContext{
  def liftMinBy[A, B : Order](f: A => B) = new LiftedMinimumBy(f)
}

trait LiftMinimumByExport{
  implicit def mkMinBy[Obj, Fn](implicit lift: ScalazLiftMinimumBy[Obj, Fn]): ScalazLiftMinimumBy.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftMinimumByPackage extends LiftMinimumByExport
  with LiftMinimumBySyntax
  with LiftMinimumByContext