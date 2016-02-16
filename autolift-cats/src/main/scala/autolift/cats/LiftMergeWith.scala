package autolift.cats

import autolift.{LiftMergeWith, LiftedMergeWith}
import cats.{Functor, Apply}

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
	implicit def unbase[FG, FH, F[_], G, H, G1 >: G, H1 >: H, Out0](implicit unapply: Un.Aux[Apply, FG, F, G], 
		                                                                     unapply2: Un.Aux[Apply, FH, F, H]): Aux[FG, FH, (G1, H1) => Out0, F[Out0]] =
		new CatsLiftMergeWith[FG, FH, (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: FG, fh: FH, f: (G1, H1) => Out0) = unapply.TC.ap{
				unapply2.TC.map(unapply2.subst(fh)){ h: H => f(_: G, h) }
			}(unapply.subst(fg))
		}
}

trait LowPriorityCatsLiftMergeWith1 extends LowPriorityCatsLiftMergeWith2{
	implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftMergeWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
		new CatsLiftMergeWith[F[G], H, Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
		}
}

trait LowPriorityCatsLiftMergeWith2{
	type Aux[Obj1, Obj2, Fn, Out0] = CatsLiftMergeWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, H, Fn](implicit unapply: Un.Apply[Functor, FG, G], 
		                                        lift: LiftMergeWith[G, H, Fn]): Aux[FG, H, Fn, unapply.M[lift.Out]] =
		new CatsLiftMergeWith[FG, H, Fn]{
			type Out = unapply.M[lift.Out]

			def apply(fg: FG, h: H, f: Fn) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, h, f) }
		}
}

trait LiftedMergeWithImplicits{
	implicit def liftedMergeWithFunctor[A, B] = new Functor[LiftedMergeWith[A, B, ?]]{
		def map[C, D](lm: LiftedMergeWith[A, B, C])(f: C => D) = lm map f
	}
}