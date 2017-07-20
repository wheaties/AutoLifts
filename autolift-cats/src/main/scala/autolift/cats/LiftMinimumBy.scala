package autolift.cats

import cats.{Functor, Foldable, Order}
import autolift.{LiftMinimumBy, LiftMinimumBySyntax}

trait CatsLiftMinimumBy[Obj, Fn] extends LiftMinimumBy[Obj, Fn] with Serializable

object CatsLiftMinimumBy extends LowPriorityCatsLiftMinimumBy{
  def apply[Obj, A](implicit lift: CatsLiftMinimumBy[Obj, A]): Aux[Obj, A, lift.Out] = lift

  implicit def base[F[_], A, B, C >: A](implicit fold: Foldable[F], ord: Order[B]): Aux[F[A], C => B, Option[A]] =
    new CatsLiftMinimumBy[F[A], C => B]{
      type Out = Option[A]

      def apply(fa: F[A], fn: C => B) = fold.reduceLeftOption(fa){
        (x: A, y: A) => if(ord.lt(fn(x), fn(y))) x else y
      }
    }
}

trait LowPriorityCatsLiftMinimumBy{
  type Aux[Obj, A, Out0] = CatsLiftMinimumBy[Obj, A]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMinimumBy[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftMinimumBy[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], fn: Fn) = functor.map(fg){ g: G => lift(g, fn) }
    }
}

final class LiftedMinimumBy[A, B : Order](f: A => B){
  def map[C : Order](g: B => C): LiftedMinimumBy[A, C] = new LiftedMinimumBy(f andThen g)

  def apply[That](that: That)(implicit lift: LiftMinimumBy[That, A => B]): lift.Out = lift(that, f)
}

trait LiftMinimumByPackage extends LiftMinimumBySyntax{
  implicit def mkMinBy[Obj, Fn](implicit lift: CatsLiftMinimumBy[Obj, Fn]): CatsLiftMinimumBy.Aux[Obj, Fn, lift.Out] = lift

  def liftMinBy[A, B : Order](f: A => B) = new LiftedMinimumBy(f)
}