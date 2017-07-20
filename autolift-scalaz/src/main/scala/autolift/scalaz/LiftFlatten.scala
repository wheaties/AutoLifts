package autolift.scalaz

import scalaz.{Functor, Bind}
import autolift.{LiftFlatten, LiftFlattenSyntax}

trait ScalazLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

object ScalazLiftFlatten extends LowPriorityScalazLiftFlatten{
  def apply[M[_], Obj](implicit lift: ScalazLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

  implicit def base[M[_], A](implicit bind: Bind[M]): Aux[M, M[M[A]], M[A]] =
    new ScalazLiftFlatten[M, M[M[A]]]{
      type Out = M[A]

      def apply(mma: M[M[A]]) = bind.bind(mma){ ma: M[A] => ma }
    }
}

trait LowPriorityScalazLiftFlatten{
  type Aux[M[_], Obj, Out0] = ScalazLiftFlatten[M, Obj]{ type Out = Out0 }

  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
    new ScalazLiftFlatten[M, F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LiftFlattenPackage extends LiftFlattenSyntax{
  implicit def mkFl[M[_], Obj](implicit lift: ScalazLiftFlatten[M, Obj]): ScalazLiftFlatten.Aux[M, Obj, lift.Out] = lift
}