package autolift.scalaz

import autolift.{LiftMerge, LiftMergeSyntax}
import scalaz.{Functor, Apply, Unapply}

trait ScalazLiftMerge[Obj1, Obj2] extends LiftMerge[Obj1, Obj2]

object ScalazLiftMerge extends LowPriorityScalazLiftMerge{
	def apply[Obj1, Obj2](implicit lift: ScalazLiftMerge[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

	implicit def base[F[_], G, H](implicit ap: Apply[F]): Aux[F[G], F[H], F[(G, H)]] =
		new ScalazLiftMerge[F[G], F[H]]{
			type Out = F[(G, H)]

			def apply(fg: F[G], fh: F[H]) = ap.tuple2(fg, fh)
		}
}

trait LowPriorityScalazLiftMerge extends LowPriorityScalazLiftMerge1{
	implicit def base[FG, FH, G, H](implicit un: Un.Apply2[Apply, FG, FH, G, H]): Aux[FG, FH, un.M[(G, H)]] =
		new ScalazLiftMerge[FG, FH]{
			type Out = un.M[(G, H)]

			def apply(fg: FG, fh: FH) = un.TC.tuple2(un._1(fg), un._2(fh))
		}
}

trait LowPriorityScalazLiftMerge1 extends LowPriorityScalazLiftMerge2{
	implicit def recur[F[_], G, H](implicit lift: LiftMerge[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
		new ScalazLiftMerge[F[G], H]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
		}
}

trait LowPriorityScalazLiftMerge2{
	type Aux[Obj1, Obj2, Out0] = ScalazLiftMerge[Obj1, Obj2]{ type Out = Out0 }

	implicit def unrecur[FG, G, H](implicit un: Un.Apply[Functor, FG, G], lift: LiftMerge[G, H]): Aux[FG, H, un.M[lift.Out]] =
		new ScalazLiftMerge[FG, H]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, h: H) = un.TC.map(un(fg)){ g: G => lift(g, h) }
		}
}

trait ScalazLiftMergeSyntax extends LiftMergeSyntax with LowPriorityLiftMergeSyntax

trait LowPriorityLiftMergeSyntax{

  /// Syntax extension providing for a `liftMerge` method.
  implicit class LowLiftMergeOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a `merge` operation, type merged dependent on the nested type structure.
     *
     * @param that the object to be merged.
     * @tparam That the argument type of the object to be merged.
     */
    def liftMerge[That](that: That)(implicit lift: LiftMerge[FA, That]): lift.Out = lift(fa, that)
  }
}