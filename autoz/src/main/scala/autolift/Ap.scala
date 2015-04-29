package autolift

import scalaz._

//TODO: ok, this is repeated 3 times...
trait Ap[FA, Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object Ap extends LowPriorityAp{
	def apply[FA, Function](implicit ap: Ap[FA, Function]): Aux[FA, Function, ap.Out] = ap

	implicit def recur[F[_], G, Function](implicit ap: Ap[G, Function], functor: Functor[F]): Aux[F[G], Function, F[ap.Out]] =
		new Ap[F[G], Function]{
			type Out = F[ap.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){g:G => ap(g, f)}
		}
}

trait LowPriorityAp{
	type Aux[FA, F, Out0] = Ap[FA, F]{ type Out = Out0 }

	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new Ap[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}