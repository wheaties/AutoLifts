package autolift.scalaz

import autolift.LiftJoin
import scalaz.{Functor, Apply}

trait ScalazLiftJoin[Obj1, Obj2] extends LiftJoin[Obj1, Obj2]

object ScalazLiftJoin extends LowPriorityScalazLiftJoin{
	def apply[Obj1, Obj2](implicit lift: ScalazLiftJoin[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

	implicit def base[F[_], G, H](implicit ap: Apply[F]): Aux[F[G], F[H], F[(G, H)]] =
		new ScalazLiftJoin[F[G], F[H]]{
			type Out = F[(G, H)]

			def apply(fg: F[G], fh: F[H]) = ap.ap(fg){ 
				ap.map(fh){ h: H => (_, h) }
			}
		}
}

trait LowPriorityScalazLiftJoin{
	type Aux[Obj1, Obj2, Out0] = ScalazLiftJoin[Obj1, Obj2]{ type Out = Out0 }

	implicit def recur[F[_], G, H](implicit lift: LiftJoin[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
		new ScalazLiftJoin[F[G], H]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
		}
}