package autolift.algebird

import autolift.DFunction2
import com.twitter.algebird.{Functor, Applicative}

trait LiftJoin[Obj1, Obj2] extends DFunction2[Obj1, Obj2]

object LiftJoin extends LowPriorityLiftJoin{
	def apply[Obj1, Obj2](implicit lift: LiftJoin[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

	implicit def base[F[_], G, H](implicit ap: Applicative[F]): Aux[F[G], F[H], F[(G, H)]] =
		new LiftJoin[F[G], F[H]]{
			type Out = F[(G, H)]

			def apply(fg: F[G], fh: F[H]) = ap.join(fg, fh)
		}
}

trait LowPriorityLiftJoin{
	type Aux[Obj1, Obj2, Out0] = LiftJoin[Obj1, Obj2]{ type Out = Out0 }

	implicit def recur[F[_], G, H](implicit lift: LiftJoin[G, H], functor: Functor[F]): Aux[F[G], H, F[lift.Out]] =
		new LiftJoin[F[G], H]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
		}
}

trait LiftJoinSyntax{
	implicit class LiftJoinOps[F[_], A](fa: F[A]){
		def liftJoin[That](that: That)(implicit lift: LiftJoin[F[A], That]): lift.Out = lift(fa, that)
	}
}

