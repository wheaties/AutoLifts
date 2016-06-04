package autolift.cats

import autolift.LiftSequence
import cats.{Traverse, Applicative, Functor}

trait CatsLiftSequence[M[_], Obj] extends LiftSequence[M, Obj]

object CatsLiftSequence extends LowPriorityCatsLiftSequence{
  def apply[M[_], Obj](implicit lift: CatsLiftSequence[M, Obj]): Aux[M, Obj, lift.Out] = lift

  implicit def base[F[_], G[_], A](implicit traverse: Traverse[F], ap: Applicative[G]): Aux[F, F[G[A]], G[F[A]]] =
    new CatsLiftSequence[F, F[G[A]]]{
      type Out = G[F[A]]

      def apply(fga: F[G[A]]) = traverse.sequence(fga)
    }
}

trait LowPriorityCatsLiftSequence{
  type Aux[M[_], Obj, Out0] = CatsLiftSequence[M, Obj]{ type Out = Out0 }

  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftSequence[M, G]): Aux[M, F[G], F[lift.Out]] =
    new CatsLiftSequence[M, F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}