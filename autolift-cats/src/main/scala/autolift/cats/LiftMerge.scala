package autolift.cats

import autolift.{LiftMerge, LiftMergeSyntax}
import cats.{Functor, Apply}

trait CatsLiftMerge[Obj1, Obj2] extends LiftMerge[Obj1, Obj2] with Serializable

object CatsLiftMerge extends LowPriorityCatsLiftMerge{
  def apply[Obj1, Obj2](implicit lift: CatsLiftMerge[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

  implicit def base[F[_], G, H](implicit ap: Apply[F]): Aux[F[G], F[H], F[(G, H)]] =
    new CatsLiftMerge[F[G], F[H]]{
      type Out = F[(G, H)]

      def apply(fg: F[G], fh: F[H]) = ap.ap{
        ap.map(fh){ h: H => (_: G, h) }
      }(fg)
    }
}

trait LowPriorityCatsLiftMerge{
  type Aux[Obj1, Obj2, Out0] = CatsLiftMerge[Obj1, Obj2]{ type Out = Out0 }

  implicit def recur[F[_], G, H](implicit lift: LiftMerge[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
    new CatsLiftMerge[F[G], H]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
    }
}

trait LiftMergePackage extends LiftMergeSyntax{
  implicit def mkJ[Obj1, Obj2](implicit lift: CatsLiftMerge[Obj1, Obj2]): CatsLiftMerge.Aux[Obj1, Obj2, lift.Out] = lift
}