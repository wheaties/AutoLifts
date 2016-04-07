package autolift.scalaz

import scalaz.{Zip, Functor, Unapply}
import autolift.{LiftZip, LiftZipSyntax}

trait ScalazLiftZip[Obj1, Obj2] extends LiftZip[Obj1, Obj2]

object ScalazLiftZip extends LowerPriorityScalazLiftZip{
	def apply[Obj1, Obj2](implicit lift: ScalazLiftZip[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

	implicit def base[F[_], G, H](implicit zip: Zip[F]): Aux[F[G], F[H], F[(G, H)]] =
		new ScalazLiftZip[F[G], F[H]]{
			type Out = F[(G, H)]

			def apply(fg: F[G], fh: F[H]) = zip.zip(fg, fh)
		}
}

trait LowerPriorityScalazLiftZip extends LowerPriorityScalazLiftZip1{
	implicit def unbase[FG, FH, G, H](implicit un: Un.Apply2[Zip, FG, FH, G, H]): Aux[FG, FH, un.M[(G, H)]] =
		new ScalazLiftZip[FG, FH]{
			type Out = un.M[(G, H)]

			def apply(fg: FG, fh: FH) = un.TC.zip(un._1(fg), un._2(fh))
		}
}

trait LowerPriorityScalazLiftZip1 extends LowerPriorityScalazLiftZip2{
	implicit def recur[F[_], G, H](implicit functor: Functor[F], lift: LiftZip[G, H]): Aux[F[G], H, F[lift.Out]] =
		new ScalazLiftZip[F[G], H]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
		}
}

trait LowerPriorityScalazLiftZip2{
	type Aux[Obj1, Obj2, Out0] = ScalazLiftZip[Obj1, Obj2]{ type Out = Out0 }

	implicit def unrecur[FG, G, H](implicit un: Un.Apply[Functor, FG, G], lift: LiftZip[G, H]): Aux[FG, H, un.M[lift.Out]] =
		new ScalazLiftZip[FG, H]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, h: H) = un.TC.map(un(fg)){ g: G => lift(g, h) }
		}
}

trait ScalazLiftZipSyntax extends LiftZipSyntax with LowPriorityLiftZipSyntax

trait LowPriorityLiftZipSyntax{

	/// Syntax extension providing for a `liftZip` method.
	implicit class LowLiftZipOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

		/**
		 * Automatic lifting of a `zip` operation, type zipped dependent on the nested type structure.
		 *
		 * @param that the object to be zipped.
		 * @tparam That the argument type of the object to be zipped.
		 */
		def liftZip[That](that: That)(implicit lift: LiftZip[FA, That]): lift.Out = lift(fa, that)
	}
}