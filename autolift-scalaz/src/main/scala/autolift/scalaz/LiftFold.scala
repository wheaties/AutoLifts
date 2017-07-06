package autolift.scalaz

import scalaz.{Functor, Foldable, Monoid}
import autolift.{LiftFold, LiftFoldSyntax}

trait ScalazLiftFold[F[_], Obj] extends LiftFold[F, Obj]

object ScalazLiftFold extends LowPriorityScalazLiftFold{
  def apply[F[_], Obj](implicit fold: ScalazLiftFold[F, Obj]): Aux[F, Obj, fold.Out] = fold

  implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
    new ScalazLiftFold[F, F[A]]{
      type Out = A

      def apply(fa: F[A]) = fold.fold(fa)
    }
}

trait LowPriorityScalazLiftFold{
  type Aux[F[_], Obj, Out0] = ScalazLiftFold[F, Obj]{ type Out = Out0 }

  implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFold[F, H]): Aux[F, G[H], G[fold.Out]] =
    new ScalazLiftFold[F, G[H]]{
      type Out = G[fold.Out]

      def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
    }
}

trait LiftFoldExport{
  implicit def mkFlA[M[_], Obj](implicit lift: ScalazLiftFold[M, Obj]): ScalazLiftFold.Aux[M, Obj, lift.Out] = lift
}

trait LiftFoldPackage extends LiftFoldExport 
  with LiftFoldSyntax