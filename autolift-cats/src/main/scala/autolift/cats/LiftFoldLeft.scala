package autolift.cats

import cats.{Functor, Foldable}
import autolift.LiftFoldLeft


trait CatsLiftFoldLeft[Obj, Fn, Z] extends LiftFoldLeft[Obj, Fn, Z]

object CatsLiftFoldLeft extends LowPriorityCatsLiftFoldLeft{
  def apply[FA, Fn, Z](implicit lift: CatsLiftFoldLeft[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

  implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (B, C) => B, B, B] =
    new CatsLiftFoldLeft[F[A], (B, C) => B, B]{
      type Out = B

      def apply(fa: F[A], f: (B, C) => B, z: B) = fold.foldLeft(fa, z)(f)
    }
}

trait LowPriorityCatsLiftFoldLeft extends LowPriorityCatsLiftFoldLeft1{
  implicit def unbase[FA, A, C >: A, B](implicit unapply: Un.Apply[Foldable, FA, A]): Aux[FA, (B, C) => B, B, B] =
    new CatsLiftFoldLeft[FA, (B, C) => B, B]{
      type Out = B

      def apply(fa: FA, f: (B, C) => B, z: B) = unapply.TC.foldLeft(unapply.subst(fa), z)(f)
    }
}

trait LowPriorityCatsLiftFoldLeft1 extends LowPriorityCatsLiftFoldLeft2{
  implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
    new CatsLiftFoldLeft[F[G], Fn, Z]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
    }
}

trait LowPriorityCatsLiftFoldLeft2{
  type Aux[FA, Fn, Z, Out0] = CatsLiftFoldLeft[FA, Fn, Z]{ type Out = Out0 }

  implicit def unrecur[FG, F[_], G, Fn, Z](implicit unapply: Un.Aux[Functor, FG, F, G], lift: LiftFoldLeft[G, Fn, Z]): Aux[FG, Fn, Z, F[lift.Out]] =
    new CatsLiftFoldLeft[FG, Fn, Z]{
      type Out = F[lift.Out]

      def apply(fg: FG, f: Fn, z: Z) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, f, z) }
    }
}

