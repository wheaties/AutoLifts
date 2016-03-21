package autolift.cats

import cats.{Functor, Foldable, Unapply}
import autolift.{LiftForAll, LiftedForAll, LiftForAllSyntax}

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

trait LowPriorityCatsLiftForAll extends LowPriorityCatsLiftForAll1{
  implicit def unbase[FA, A, C >: A](implicit unapply: Un.Apply[Foldable, FA, A]): Aux[FA, C => Boolean, Boolean] =
    new CatsLiftForAll[FA, C => Boolean]{
      type Out = Boolean

      def apply(fa: FA, f: C => Boolean) = unapply.TC.forall(unapply.subst(fa))(f)
    }
}

trait LowPriorityCatsLiftForAll1 extends LowPriorityCatsLiftForAll2{
  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftForAll[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftForAll[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait CatLiftForAllSyntax extends LiftForAllSyntax with LowPriorityLiftForAllSyntax

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

trait LowPriorityCatsLiftForAll2{
  type Aux[Obj, Fn, Out0] = CatsLiftForAll[Obj, Fn]{ type Out = Out0 }

  implicit def unrecur[FG, G, Fn](implicit unapply: Un.Apply[Functor, FG, G], lift: LiftForAll[G, Fn]): Aux[FG, Fn, unapply.M[lift.Out]] =
    new CatsLiftForAll[FG, Fn]{
      type Out = unapply.M[lift.Out]

      def apply(fg: FG, f: Fn) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, f) }
    }
}

