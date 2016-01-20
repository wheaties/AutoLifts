package autolift.cats

import cats.{MonadFilter, Functor}
import autolift.LiftFilter

trait CatsLiftFilter[Obj, Fn] extends LiftFilter[Obj, Fn]

object CatsLiftFilter extends LowPriorityCatsLiftFilter{
  def apply[Obj, Fn](implicit lift: LiftFilter[Obj, Fn]) = lift

  implicit def plus[M[_], A, B >: A](implicit mp: MonadFilter[M]) =
    new CatsLiftFilter[M[A], B => Boolean]{
      def apply(ma: M[A], pred: B => Boolean) = mp.filter(ma)(pred)
    }
}

trait LowPriorityCatsLiftFilter{

  implicit def recur[F[_], G, Fn](implicit lift: LiftFilter[G, Fn], functor: Functor[F]) =
    new CatsLiftFilter[F[G], Fn]{
      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

