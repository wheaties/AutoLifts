package autolift.cats

import cats.{FlatMap, Functor, Unapply}
import autolift.{LiftFlatten, LiftFlattenSyntax}

trait CatsLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

object CatsLiftFlatten extends LowPriorityCatsLiftFlatten{
  def apply[M[_], Obj](implicit lift: CatsLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

  implicit def base[M[_], A](implicit flatMap: FlatMap[M]): Aux[M, M[M[A]], M[A]] =
    new CatsLiftFlatten[M, M[M[A]]]{
      type Out = M[A]

      def apply(mma: M[M[A]]) = flatMap.flatMap(mma){ ma: M[A] => ma }
    }
}

trait LowPriorityCatsLiftFlatten extends LowPriorityCatsLiftFlatten1{
  implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
    new CatsLiftFlatten[M, F[G]]{
      type Out = F[lift.Out]

      def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
    }
}

trait LowPriorityCatsLiftFlatten1{
  type Aux[M[_], Obj, Out0] = CatsLiftFlatten[M, Obj]{ type Out = Out0 }

  implicit def unrecur[M[_], FG, F[_], G](implicit unapply: Un.Apply[Functor, FG, G], lift: LiftFlatten[M, G]): Aux[M, FG, unapply.M[lift.Out]] =
    new CatsLiftFlatten[M, FG]{
      type Out = unapply.M[lift.Out]

      def apply(fg: FG) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g) }
    }
}

trait CatsLiftFlattenSyntax extends LiftFlattenSyntax with LowPriorityLiftFlattenSyntax

trait LowPriorityLiftFlattenSyntax{

  ///Syntax extension providing for a `liftFlatten` method.
  implicit class LowLiftFlattenOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a flatten operation given the juxtaposition of the two of the given types in the nested type 
     * structure.
     *
     * @tparam M the type over which to flatten given that there exists the concept of flattening of the type.
     */
    def liftFlatten[M[_]](implicit lift: LiftFlatten[M, FA]): lift.Out = lift(fa)
  }
}

trait LiftFlattenExport{
  implicit def mkFl[M[_], Obj](implicit lift: CatsLiftFlatten[M, Obj]): CatsLiftFlatten.Aux[M, Obj, lift.Out] = lift
}

trait LiftFlattenPackage extends LiftFlattenExport
  with CatsLiftFlattenSyntax