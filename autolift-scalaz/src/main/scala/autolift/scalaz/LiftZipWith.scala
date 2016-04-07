package autolift.scalaz

import scalaz.{Zip, Functor, Unapply}
import autolift.{LiftZipWith, LiftedZipWith, LiftZipWithSyntax}

trait ScalazLiftZipWith[Obj1, Obj2, Fn] extends LiftZipWith[Obj1, Obj2, Fn]

object ScalazLiftZipWith extends LowerPriorityScalazLiftZipWith{
	def apply[Obj1, Obj2, Fn](implicit lift: ScalazLiftZipWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def zipped[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit zip: Zip[F], functor: Functor[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
		new ScalazLiftZipWith[F[G], F[H], (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = zip.zipWith(fg, fh)(f)
		}
}

trait LowerPriorityScalazLiftZipWith extends LowerPriorityScalazLiftZipWith1{
	implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftZipWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
		new ScalazLiftZipWith[F[G], H, Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
		}
}

trait LowerPriorityScalazLiftZipWith1{
	type Aux[Obj1, Obj2, Fn, Out0] = ScalazLiftZipWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def unrecur[FG, G, H, Fn](implicit un: Un.Apply[Functor, FG, G], lift: LiftZipWith[G, H, Fn]): Aux[FG, H, Fn, un.M[lift.Out]] =
		new ScalazLiftZipWith[FG, H, Fn]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, h: H, f: Fn) = un.TC.map(un(fg)){ g: G => lift(g, h, f) }
		}
}

trait ScalazLiftZipWithSyntax extends LiftZipWithSyntax with LowPriorityLiftZipWithSyntax

trait LowPriorityLiftZipWithSyntax{

	/// Syntax extension providing for a `liftZipWith` method.
	implicit class LowLiftZipWithOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

		/**
		 * Automatic lifting of a `zip` operation based upon the application of a function.
		 *
		 * @param that the object to be zipped.
		 * @param f the function over which to zip
		 * @tparam That the type of the object to be zipped
		 * @tparam B the first argument of the function used in the zipping
		 * @tparam C the second argument of the function used in the zipping
		 * @tparam D the return type of the function used in the zipping
		 */
		def liftZipWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftZipWith[FA, That, (B, C) => D]): lift.Out = 
			lift(fa, that, f)
	}
}

trait LiftedZipWithImplicits{
	implicit def liftedZipWithFunctor[A, B] = new Functor[LiftedZipWith[A, B, ?]]{
		def map[C, D](lm: LiftedZipWith[A, B, C])(f: C => D) = lm map f
	}
}