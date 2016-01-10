package autolift.cats

import cats.{Functor, Monoid, Foldable}
import autolift.LiftFoldAt
import export._

trait CatsLiftFoldAt[F[_], Obj] extends LiftFoldAt[F, Obj]

@exports(Subclass)
object CatsLiftFoldAt extends LowPriorityCatsLiftFoldAt{
  def apply[F[_], Obj](implicit fold: CatsLiftFoldAt[F, Obj]): Aux[F, Obj, fold.Out] = fold

  @export(Subclass)
  implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
    new CatsLiftFoldAt[F, F[A]]{
      type Out = A

      def apply(fa: F[A]) = fold.fold(fa)
    }
}

trait LowPriorityCatsLiftFoldAt{
  type Aux[F[_], Obj, Out0] = CatsLiftFoldAt[F, Obj]{ type Out = Out0 }

  @export(Subclass)
  implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFoldAt[F, H]): Aux[F, G[H], G[fold.Out]] =
    new CatsLiftFoldAt[F, G[H]]{
      type Out = G[fold.Out]

      def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
    }
}
