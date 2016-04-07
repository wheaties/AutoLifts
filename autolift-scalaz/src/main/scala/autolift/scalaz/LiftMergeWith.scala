package autolift.scalaz

import autolift.{LiftMergeWith, LiftedMergeWith, LiftMergeWithSyntax}
import scalaz.{Functor, Apply, Unapply}

trait ScalazLiftMergeWith[Obj1, Obj2, Fn] extends LiftMergeWith[Obj1, Obj2, Fn]

object ScalazLiftMergeWith extends LowPriorityScalazLiftMergeWith{
	def apply[Obj1, Obj2, Fn](implicit lift: ScalazLiftMergeWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Apply[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
		new ScalazLiftMergeWith[F[G], F[H], (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.apply2(fg, fh)(f)
		}
}

trait LowPriorityScalazLiftMergeWith extends LowPriorityScalazLiftMergeWith1{
	implicit def unbase[FG, FH, G, H, G1 >: G, H1 >: H, Out0](implicit un: Un.Apply2[Apply, FG, FH, G, H]): Aux[FG, FH, (G1, H1) => Out0, un.M[Out0]] =
		new ScalazLiftMergeWith[FG, FH, (G1, H1) => Out0]{
			type Out = un.M[Out0]

			def apply(fg: FG, fh: FH, f: (G1, H1) => Out0) = un.TC.apply2(un._1(fg), un._2(fh))(f)
		}
}

trait LowPriorityScalazLiftMergeWith1 extends LowPriorityScalazLiftMergeWith2{
	implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftMergeWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
		new ScalazLiftMergeWith[F[G], H, Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
		}
}

trait LowPriorityScalazLiftMergeWith2{
	type Aux[Obj1, Obj2, Fn, Out0] = ScalazLiftMergeWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, H, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftMergeWith[G, H, Fn]): Aux[FG, H, Fn, un.M[lift.Out]] =
		new ScalazLiftMergeWith[FG, H, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, h: H, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, h, f) }
		}
}

trait ScalazLiftMergeWithSyntax extends LiftMergeWithSyntax with LowPriorityLiftMergeWithSyntax

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