package autolift.algebird

import autolift.LiftMerge
import com.twitter.algebird.{Functor, Applicative}

trait AlgeLiftMerge[Obj1, Obj2] extends LiftMerge[Obj1, Obj2]

object AlgeLiftMerge extends LowPriorityAlgeLiftMerge{
  def apply[Obj1, Obj2](implicit lift: AlgeLiftMerge[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

  implicit def base[F[_], G, H](implicit ap: Applicative[F]): Aux[F[G], F[H], F[(G, H)]] =
    new AlgeLiftMerge[F[G], F[H]]{
      type Out = F[(G, H)]

      def apply(fg: F[G], fh: F[H]) = ap.join(fg, fh)
    }
}

trait LowPriorityAlgeLiftMerge{
  type Aux[Obj1, Obj2, Out0] = AlgeLiftMerge[Obj1, Obj2]{ type Out = Out0 }

  implicit def recur[F[_], G, H](implicit lift: LiftMerge[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
    new AlgeLiftMerge[F[G], H]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
    }
}

trait LiftJoinSyntax{
  implicit class LiftJoinOps[F[_], A](fa: F[A]){
    def liftJoin[That](that: That)(implicit lift: AlgeLiftMerge[F[A], That]): lift.Out = lift(fa, that)
  }
}