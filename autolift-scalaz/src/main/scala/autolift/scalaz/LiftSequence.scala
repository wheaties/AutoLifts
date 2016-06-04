package autolift.scalaz

import autolift.LiftSequence
import scalaz.{Traverse, Applicative, Functor}

trait ScalazLiftSequence[M[_], Obj] extends LiftSequence[M, Obj]

object ScalazLiftSequence extends LowPriorityScalazLiftSequence{
  def apply[M[_], Obj](implicit lift: ScalazLiftSequence[M, Obj]): Aux[M, Obj, lift.Out] = lift

  implicit def base[F[_], G[_], A](implicit traverse: Traverse[F], ap: Applicative[G]): Aux[F, F[G[A]], G[F[A]]] =
    new ScalazLiftSequence[F, F[G[A]]]{
      type Out = G[F[A]]

      def apply(fga: F[G[A]]) = traverse.sequence(fga)
    }
}

trait LowPriorityScalazLiftSequence{
  type Aux[M[_], Obj, Out0] = ScalazLiftSequence[M, Obj]{ type Out = Out0 }

  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftSequence[M, G]): Aux[M, F[G], F[lift.Out]] =
    new ScalazLiftSequence[M, F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}