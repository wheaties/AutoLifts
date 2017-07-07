package autolift.algebird

import autolift.LiftFlatten
import com.twitter.algebird.{Monad, Functor}

trait AlgeLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

object AlgeLiftFlatten extends LowPriorityAlgeLiftFlatten{
  def apply[M[_], Obj](implicit lift: AlgeLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

  implicit def base[M[_], A](implicit fm: Monad[M]): Aux[M, M[M[A]], M[A]] =
    new AlgeLiftFlatten[M, M[M[A]]]{
      type Out = M[A]

      def apply(mma: M[M[A]]) = fm.flatMap(mma){ ma: M[A] => ma }
    }
}

trait LowPriorityAlgeLiftFlatten{
  type Aux[M[_], Obj, Out0] = AlgeLiftFlatten[M, Obj]{ type Out = Out0 }

  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
    new AlgeLiftFlatten[M, F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

