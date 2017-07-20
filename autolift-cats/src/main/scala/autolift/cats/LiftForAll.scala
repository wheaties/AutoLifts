package autolift.cats

import cats.{Functor, Foldable}
import autolift.{LiftForAll, LiftForAllSyntax, LiftForAllContext}

//TODO: syntax is currently forAll vs forall. Make consistent?
trait CatsLiftForAll[Obj, Fn] extends LiftForAll[Obj, Fn] with Serializable

object CatsLiftForAll extends LowPriorityCatsLiftForAll {
  def apply[Obj, Fn](implicit lift: CatsLiftForAll[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]): Aux[F[A], C => Boolean, Boolean] =
    new CatsLiftForAll[F[A], C => Boolean]{
      type Out = Boolean

      def apply(fa: F[A], f: C => Boolean) = fold.forall(fa)(f)
    }
}

trait LowPriorityCatsLiftForAll{
  type Aux[Obj, Fn, Out0] = CatsLiftForAll[Obj, Fn]{ type Out = Out0 }
  
  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftForAll[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftForAll[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait LiftForAllPackage extends LiftForAllSyntax with LiftForAllContext{
  implicit def mkAll[Obj, Fn](implicit lift: CatsLiftForAll[Obj, Fn]): CatsLiftForAll.Aux[Obj, Fn, lift.Out] = lift
}