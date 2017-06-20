package autolift.cats

import cats.{Functor, Unapply}
import autolift.{LiftMap, LiftedMap, LiftMapSyntax, LiftMapContext}

trait CatsLiftMap[Obj, Fn] extends LiftMap[Obj, Fn]

object CatsLiftMap extends LowPriorityCatsLiftMap {
  def apply[Obj, Fn](implicit lift: CatsLiftMap[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
    new CatsLiftMap[F[A], C => B]{
      type Out = F[B]

      def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
    }
}

trait LowPriorityCatsLiftMap{
  type Aux[Obj, Fn, Out0] = CatsLiftMap[Obj, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftMap[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait CatsLiftMapSyntax extends LiftMapSyntax with LowPriorityLiftMapSyntax

trait LowPriorityLiftMapSyntax{

  /// Syntax extension providing for a `liftMap` method on an object with shape differing from F[A].
  implicit class LowLiftMapOps[MA](ma: MA)(implicit ev: Unapply[Functor, MA]){

    /**
     * Automatic lifting of the function `f` over the object such that the application point is dictated by the type
     * of function invocation.
     *
     * @param f the function to be lifted.
     * @tparam B the argument type of the function.
     * @tparam C the return type of the function.
     */
    def liftMap[B, C](f: B => C)(implicit lift: LiftMap[MA, B => C]): lift.Out = lift(ma, f)
  }
}

trait LiftedMapImplicits{
  implicit def liftedMapFunctor[A] = new Functor[LiftedMap[A, ?]]{
    def map[B, C](lm: LiftedMap[A, B])(f: B => C) = lm map f
  }
}

trait LiftMapExport{
  implicit def mkM[Obj, Fn](implicit lift: CatsLiftMap[Obj, Fn]): CatsLiftMap.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftMapPackage extends LiftMapExport
  with LiftedMapImplicits
  with CatsLiftMapSyntax
  with LiftMapContext