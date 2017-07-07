package autolift.scalaz

import scalaz.Functor
import autolift.{LiftMap, LiftedMap, LiftMapSyntax, LiftMapContext}

trait ScalazLiftMap[Obj, Fn] extends LiftMap[Obj, Fn]

object ScalazLiftMap extends LowPriorityScalazLiftMap {
  def apply[Obj, Fn](implicit lift: ScalazLiftMap[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
    new ScalazLiftMap[F[A], C => B]{
      type Out = F[B]

      def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
    }
}

trait LowPriorityScalazLiftMap{
  type Aux[Obj, Fn, Out0] = ScalazLiftMap[Obj, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new ScalazLiftMap[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait LiftedMapImplicits{
  implicit def liftedMapFunctor[A] = new Functor[LiftedMap[A, ?]]{
    def map[B, C](lm: LiftedMap[A, B])(f: B => C) = lm map f
  }
}

trait LiftMapExport{
  implicit def mkM[Obj, Fn](implicit lift: ScalazLiftMap[Obj, Fn]): ScalazLiftMap.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftMapPackage extends LiftMapExport
  with LiftedMapImplicits
  with LiftMapSyntax
  with LiftMapContext