package autolift.algebird

import autolift.{LiftMergeWith, LiftedMergeWith}
import com.twitter.algebird.{Functor, Applicative}

trait AlgeLiftMergeWith[Obj1, Obj2, Fn] extends LiftMergeWith[Obj1, Obj2, Fn]

object AlgeLiftMergeWith extends LowPriorityAlgeLiftMergeWith{
  def apply[Obj1, Obj2, Fn](implicit lift: AlgeLiftMergeWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

  implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Applicative[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
    new AlgeLiftMergeWith[F[G], F[H], (G1, H1) => Out0]{
      type Out = F[Out0]

      def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.joinWith(fg, fh)(f)
    }
}

trait LowPriorityAlgeLiftMergeWith{
  type Aux[Obj1, Obj2, Fn, Out0] = AlgeLiftMergeWith[Obj1, Obj2, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftMergeWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
    new AlgeLiftMergeWith[F[G], H, Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
    }
}

trait LiftJoinWithSyntax{
  implicit class LiftJoinWithOps[F[_], A](fa: F[A]){
    def liftJoinWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftMergeWith[F[A], That, (B, C) => D]): lift.Out =
      lift(fa, that, f)
  }
}

trait LiftJoinWithContext{
  def liftJoinWith[A, B, C](f: (A, B) => C): LiftedJoinWith[A, B, C] = new LiftedMergeWith(f)

  type LiftedJoinWith[A, B, C] = LiftedMergeWith[A, B, C]
}