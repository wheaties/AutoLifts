package autolift.cats

import cats.{Functor, Foldable, Order}
import autolift.{LiftMaximumBy, LiftMaximumBySyntax}

trait CatsLiftMaximumBy[Obj, Fn] extends LiftMaximumBy[Obj, Fn] with Serializable

object CatsLiftMaximumBy extends LowPriorityCatsLiftMaximumBy{
  def apply[Obj, A](implicit lift: CatsLiftMaximumBy[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A, B, C >: A](implicit fold: Foldable[F], ord: Order[B]): Aux[F[A], C => B, Option[A]] =
    new CatsLiftMaximumBy[F[A], C => B]{
      type Out = Option[A]

      def apply(fa: F[A], fn: C => B) = fold.reduceLeftOption(fa){
        (x: A, y: A) => if(ord.gt(fn(x), fn(y))) x else y
      }
    }
}

trait LowPriorityCatsLiftMaximumBy{
  type Aux[Obj, A, Out0] = CatsLiftMaximumBy[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMaximumBy[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftMaximumBy[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], fn: Fn) = functor.map(fg){ g: G => lift(g, fn) }
    }
}

final class LiftedMaximumBy[A, B : Order](f: A => B){
  def map[C : Order](g: B => C): LiftedMaximumBy[A, C] = new LiftedMaximumBy(f andThen g)

  def apply[That](that: That)(implicit lift: LiftMaximumBy[That, A => B]): lift.Out = lift(that, f)
}

trait LiftMaximumByPackage extends LiftMaximumBySyntax{
  implicit def mkMaxBy[Obj, Fn](implicit lift: CatsLiftMaximumBy[Obj, Fn]): CatsLiftMaximumBy.Aux[Obj, Fn, lift.Out] = lift

  def liftMaxBy[A, B : Order](f: A => B) = new LiftedMaximumBy(f)
}