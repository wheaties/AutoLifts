package autolift.cats

import cats.{FlatMap, Functor}
import autolift.{LiftFlatten, LiftFlattenSyntax}

trait CatsLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj] with Serializable

object CatsLiftFlatten extends LowPriorityCatsLiftFlatten{
  def apply[M[_], Obj](implicit lift: CatsLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

  implicit def base[M[_], A](implicit flatMap: FlatMap[M]): Aux[M, M[M[A]], M[A]] =
    new CatsLiftFlatten[M, M[M[A]]]{
      type Out = M[A]

      def apply(mma: M[M[A]]) = flatMap.flatMap(mma){ ma: M[A] => ma }
    }
}

trait LowPriorityCatsLiftFlatten{
  type Aux[M[_], Obj, Out0] = CatsLiftFlatten[M, Obj]{ type Out = Out0 }

  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
    new CatsLiftFlatten[M, F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LiftFlattenExport{
  implicit def mkFl[M[_], Obj](implicit lift: CatsLiftFlatten[M, Obj]): CatsLiftFlatten.Aux[M, Obj, lift.Out] = lift
}

trait LiftFlattenPackage extends LiftFlattenExport
  with LiftFlattenSyntax