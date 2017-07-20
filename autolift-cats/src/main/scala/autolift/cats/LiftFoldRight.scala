package autolift.cats

import cats.{Eval, Functor, Foldable}
import autolift.LiftFoldRight


trait CatsLiftFoldRight[FA, Fn, Z] extends LiftFoldRight[FA, Fn, Z] with Serializable

object CatsLiftFoldRight extends LowPriorityCatsLiftFoldRight{
  def apply[FA, Fn, Z](implicit lift: CatsLiftFoldRight[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

  implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (C, Eval[B]) => Eval[B], Eval[B], Eval[B]] =
    new CatsLiftFoldRight[F[A], (C, Eval[B]) => Eval[B], Eval[B]]{
      type Out = Eval[B]

      def apply(fa: F[A], f: (C, Eval[B]) => Eval[B], z: Eval[B]) = fold.foldRight(fa, z)(f)
    }
}

trait LowPriorityCatsLiftFoldRight{
  type Aux[FA, Fn, Z, Out0] = CatsLiftFoldRight[FA, Fn, Z]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldRight[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
    new CatsLiftFoldRight[F[G], Fn, Z]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
    }
}

final class LiftedFoldRight[B, Z](z: Eval[Z], f: (B, Eval[Z]) => Eval[Z]) {
  def apply[That](that: That)(implicit lift: LiftFoldRight[That, (B, Eval[Z]) => Eval[Z], Eval[Z]]): lift.Out = lift(that, f, z)
}

trait LiftFoldRightPackage{
  implicit class LiftFoldRightOps[F[_], A](fa: F[A]){
    def liftFoldRight[B, Z](z: Eval[Z])(f: (B, Eval[Z]) => Eval[Z])(implicit lift: LiftFoldRight[F[A], (B, Eval[Z]) => Eval[Z], Eval[Z]]): lift.Out =
      lift(fa, f, z)
  }

  implicit def mkFldR[Obj, Fn, Z](implicit lift: CatsLiftFoldRight[Obj, Fn, Z]): CatsLiftFoldRight.Aux[Obj, Fn, Z, lift.Out] = lift

  def liftFoldRight[B, Z](z: Eval[Z])(f: (B, Eval[Z]) => Eval[Z]) = new LiftedFoldRight(z, f)
}