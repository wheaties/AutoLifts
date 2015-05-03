package autolift

import scalaz._

trait FlatMapper[FA, -Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object FlatMapper extends LowPriorityFlatMapper{
	def apply[FA, Function](implicit fm: FlatMapper[FA, Function]): Aux[FA, Function, fm.Out] = fm

	implicit def recur[F[_], G, Function](implicit fm: FlatMapper[G, Function], functor: Functor[F]): Aux[F[G], Function, F[fm.Out]] =
		new FlatMapper[F[G], Function]{
			type Out = F[fm.Out]

			def apply(fa: F[G], f: Function) = functor.map(fa){g: G => fm(g, f)}
		}
}

trait LowPriorityFlatMapper{
	type Aux[FA, F, Out0] = FlatMapper[FA, F]{ type Out = Out0 }

	implicit def base[M[_], A, B](implicit bind: Bind[M]): Aux[M[A], A => M[B], M[B]] =
		new FlatMapper[M[A], A => M[B]]{
			type Out = M[B]

			def apply(ga: M[A], f: A => M[B]) = bind.bind(ga)(f)
		}
}