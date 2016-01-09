package autolift.cats

import cats.{Functor, Foldable}
import autolift.LiftFoldRight
import export._


trait CatsLiftFoldRight[FA, Fn, Z] extends LiftFoldRight[FA, Fn, Z]

@exports(Subclass)
object CatsLiftFoldRight extends LowPriorityCatsLiftFoldRight{
  def apply[FA, Fn, Z](implicit lift: CatsLiftFoldRight[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

  @export(Subclass)
  implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (C, => B) => B, B, B] =
    new CatsLiftFoldRight[F[A], (C, => B) => B, B]{
      type Out = B

      //TODO: Update to Eval lazyFoldRight cats-like
      def apply(fa: F[A], f: (C, => B) => B, z: B) = ??? //fold.foldRight(fa, z)(f)
    }
}

trait LowPriorityCatsLiftFoldRight{
  type Aux[FA, Fn, Z, Out0] = CatsLiftFoldRight[FA, Fn, Z]{ type Out = Out0 }

  @export(Subclass)
  implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldRight[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
    new CatsLiftFoldRight[F[G], Fn, Z]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
    }
}

trait LiftFoldRightSyntax{
  implicit class LiftFoldRightOps[F[_], A](fa: F[A]){
    def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z)(implicit lift: LiftFoldRight[F[A], (B, => Z) => Z, Z]): lift.Out =
      lift(fa, f, z)
  }
}

final class LiftedFoldRight[B, Z](z: Z, f: (B, => Z) => Z){
  def apply[That](that: That)(implicit lift: LiftFoldRight[That, (B, => Z) => Z, Z]): lift.Out = lift(that, f, z)
}

trait LiftFoldRightContext{
  def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z) = new LiftedFoldRight(z, f)
}
