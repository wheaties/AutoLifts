package autolift.cats

import autolift.{LiftMergeWith, LiftedMergeWith, LiftMergeWithSyntax, LiftMergeWithContext}
import cats.{Functor, Apply, Unapply}

trait CatsLiftMergeWith[Obj1, Obj2, Fn] extends LiftMergeWith[Obj1, Obj2, Fn]

object CatsLiftMergeWith extends LowPriorityCatsLiftMergeWith{
  def apply[Obj1, Obj2, Fn](implicit lift: CatsLiftMergeWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

  implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Apply[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
    new CatsLiftMergeWith[F[G], F[H], (G1, H1) => Out0]{
      type Out = F[Out0]

      def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.ap{
        ap.map(fh){ h: H => f(_: G, h) }
      }(fg)
    }
}

trait LowPriorityCatsLiftMergeWith extends LowPriorityCatsLiftMergeWith1{
  implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftMergeWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
    new CatsLiftMergeWith[F[G], H, Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
    }
}

trait LowPriorityCatsLiftMergeWith1{
  type Aux[Obj1, Obj2, Fn, Out0] = CatsLiftMergeWith[Obj1, Obj2, Fn]{ type Out = Out0 }

  implicit def unrecur[FG, G, H, Fn](implicit unapply: Un.Apply[Functor, FG, G], 
                                              lift: LiftMergeWith[G, H, Fn]): Aux[FG, H, Fn, unapply.M[lift.Out]] =
    new CatsLiftMergeWith[FG, H, Fn]{
      type Out = unapply.M[lift.Out]

      def apply(fg: FG, h: H, f: Fn) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, h, f) }
    }
}

trait CatsLiftMergeWithSyntax extends LiftMergeWithSyntax with LowPriorityLiftMergeWithSyntax

trait LowPriorityLiftMergeWithSyntax{

  /// Syntax extension providing for a `liftMergeWith` method.
  implicit class LowLiftMergeWithOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a `merging` operation based upon the application of a function.
     *
     * @param that the object to be merged
     * @param f the function over which to merge
     * @tparam That the type of the object to be merged
     * @tparam B the first argument of the function used in the merging
     * @tparam C the second argument of the function used in the merging
     * @tparam D the return type of the function used in the merging
     */
    def liftMergeWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftMergeWith[FA, That, (B, C) => D]): lift.Out = 
      lift(fa, that, f)
  }
}

trait LiftedMergeWithImplicits{
  implicit def liftedMergeWithFunctor[A, B] = new Functor[LiftedMergeWith[A, B, ?]]{
    def map[C, D](lm: LiftedMergeWith[A, B, C])(f: C => D) = lm map f
  }
}

trait LiftMergeWithExport{
  implicit def mkJw[Obj1, Obj2, Fn](implicit lift: CatsLiftMergeWith[Obj1, Obj2, Fn]): CatsLiftMergeWith.Aux[Obj1, Obj2, Fn, lift.Out] = lift
}

trait LiftMergeWithPackage extends LiftMergeWithExport
  with LiftedMergeWithImplicits
  with CatsLiftMergeWithSyntax
  with LiftMergeWithContext