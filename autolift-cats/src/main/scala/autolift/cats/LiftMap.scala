package autolift.cats

import cats.{Functor, Unapply}
import autolift.{LiftMap, LiftedMap, LiftMapSyntax}

trait CatsLiftMap[Obj, Fn] extends LiftMap[Obj, Fn]

object CatsLiftMap extends LowPriorityCatsLiftMap {
  def apply[Obj, Fn](implicit lift: CatsLiftMap[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
    new CatsLiftMap[F[A], C => B]{
      type Out = F[B]

      def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
    }
}

trait LowPriorityCatsLiftMap extends LowPriorityCatsLiftMap1{
  implicit def unbase[MA, A, C >: A, B](implicit unapply: Un.Apply[Functor, MA, A]): Aux[MA, C => B, unapply.M[B]] =
    new CatsLiftMap[MA, C => B]{
      type Out = unapply.M[B]

      def apply(ma: MA, f: C => B) = unapply.TC.map(unapply.subst(ma))(f)
    }
}

trait LowPriorityCatsLiftMap1 extends LowPriorityCatsLiftMap2{
  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftMap[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait LowPriorityCatsLiftMap2{
  type Aux[Obj, Fn, Out0] = CatsLiftMap[Obj, Fn]{ type Out = Out0 }

  implicit def unbase[FG, G, Fn](implicit unapply: Un.Apply[Functor, FG, G], lift: LiftMap[G, Fn]): Aux[FG, Fn, unapply.M[lift.Out]] =
    new CatsLiftMap[FG, Fn]{
      type Out = unapply.M[lift.Out]

      def apply(fg: FG, f: Fn) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, f) }
    }
}

trait LiftedMapImplicits{
  implicit def liftedMapFunctor[A] = new Functor[LiftedMap[A, ?]]{
    def map[B, C](lm: LiftedMap[A, B])(f: B => C) = lm map f
  }
}

