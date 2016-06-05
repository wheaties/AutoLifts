package autolift.scalaz

import autolift.LiftReverse
import scalaz.{Traverse, Functor}

trait ScalazLiftReverse[M[_], Obj] extends LiftReverse[M, Obj]

object ScalazLiftReverse extends LowPriorityScalazLiftReverse{
  def apply[M[_], Obj](implicit lift: ScalazLiftReverse[M, Obj]) = lift

  implicit def base[F[_], A](implicit traverse: Traverse[F]) =
    new ScalazLiftReverse[F, F[A]]{
      def apply(fga: F[A]) = traverse.reverse(fga)
    }
}

trait LowPriorityScalazLiftReverse{
  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftReverse[M, G]) =
    new ScalazLiftReverse[M, F[G]]{
      def apply(fg: F[G]) = functor.map(fg)(lift)
    }
}