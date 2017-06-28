package autolift.scalaz

import autolift.LiftMerge
import scalaz.{Functor, Apply}

trait ScalazLiftMerge[Obj1, Obj2] extends LiftMerge[Obj1, Obj2]

object ScalazLiftMerge extends LowPriorityScalazLiftMerge{
  def apply[Obj1, Obj2](implicit lift: ScalazLiftMerge[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

  implicit def base[F[_], G, H](implicit ap: Apply[F]): Aux[F[G], F[H], F[(G, H)]] =
    new ScalazLiftMerge[F[G], F[H]]{
      type Out = F[(G, H)]

      def apply(fg: F[G], fh: F[H]) = ap.tuple2(fg, fh)
    }
}

trait LowPriorityScalazLiftMerge {
  type Aux[Obj1, Obj2, Out0] = ScalazLiftMerge[Obj1, Obj2]{ type Out = Out0 }

  implicit def recur[F[_], G, H](implicit lift: LiftMerge[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
    new ScalazLiftMerge[F[G], H]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
    }
}