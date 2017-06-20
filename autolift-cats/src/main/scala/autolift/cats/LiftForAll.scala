package autolift.cats

import cats.{Functor, Foldable, Unapply}
import autolift.{LiftForAll, LiftForAllSyntax, LiftForAllContext}

//TODO: syntax is currently forAll vs forall. Make consistent?
trait CatsLiftForAll[Obj, Fn] extends LiftForAll[Obj, Fn]

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

trait CatsLiftForAllSyntax extends LiftForAllSyntax with LowPriorityLiftForAllSyntax

trait LowPriorityLiftForAllSyntax{

  /// Syntax extension providing for a `liftForAll` method.
  implicit class LowLiftForAllOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of the predicate `f` over the object such that the application point is dictated by the type
     * of predicate invocation.
     *
     * @param f the predicate to be lifted.
     * @tparam B the argument type of the predicate.
     */
    def liftForAll[B](f: B => Boolean)(implicit lift: LiftForAll[FA, B => Boolean]): lift.Out = lift(fa, f)
  }
}

trait LiftForAllExport{
  implicit def mkAll[Obj, Fn](implicit lift: CatsLiftForAll[Obj, Fn]): CatsLiftForAll.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftForAllPackage extends LiftForAllExport
  with CatsLiftForAllSyntax
  with LiftForAllContext