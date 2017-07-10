package autolift.scalaz

import scalaz.{Functor, Foldable, Order}
import autolift.{LiftMaximumBy, LiftMaximumBySyntax}

trait ScalazLiftMaximumBy[Obj, Fn] extends LiftMaximumBy[Obj, Fn] with Serializable

object ScalazLiftMaximumBy extends LowPriorityScalazLiftMaximumBy{
  def apply[Obj, A](implicit lift: ScalazLiftMaximumBy[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A, B, C >: A](implicit fold: Foldable[F], ord: Order[B]): Aux[F[A], C => B, Option[A]] =
    new ScalazLiftMaximumBy[F[A], C => B]{
      type Out = Option[A]

      def apply(fa: F[A], fn: C => B) = fold.maximumBy(fa)(fn)
    }
}

trait LowPriorityScalazLiftMaximumBy{
  type Aux[Obj, A, Out0] = ScalazLiftMaximumBy[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMaximumBy[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new ScalazLiftMaximumBy[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], fn: Fn) = functor.map(fg){ g: G => lift(g, fn) }
    }
}

final class LiftedMaximumBy[A, B : Order](f: A => B){
  def map[C : Order](g: B => C): LiftedMaximumBy[A, C] = new LiftedMaximumBy(f andThen g)

  def apply[That](that: That)(implicit lift: LiftMaximumBy[That, A => B]): lift.Out = lift(that, f)
}

trait LiftMaximumByContext{
  def liftMaxBy[A, B : Order](f: A => B) = new LiftedMaximumBy(f)
}

trait LiftMaximumByExport{
  implicit def mkMaxBy[Obj, Fn](implicit lift: ScalazLiftMaximumBy[Obj, Fn]): ScalazLiftMaximumBy.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftMaximumByPackage extends LiftMaximumByExport
  with LiftMaximumBySyntax
  with LiftMaximumByContext