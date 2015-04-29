package autolift

import scalaz._

trait Mapper[FA, Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object Mapper extends LowPriorityMapper{
	def apply[FA, Function](implicit mapper: Mapper[FA, Function]): Aux[FA, Function, mapper.Out] = mapper

	implicit def recur[F[_], G, Function](implicit mapper: Mapper[G, Function], functor: Functor[F]): Aux[F[G], Function, F[mapper.Out]] =
		new Mapper[F[G], Function]{
			type Out = F[mapper.Out]

			def apply(fa: F[G], f: Function) = functor.map(fa){g: G => mapper(g, f)}
		}
}

trait LowPriorityMapper{
	type Aux[FA, F, Out0] = Mapper[FA, F]{ type Out = Out0 }

	implicit def base[G[_], A, B](implicit functor: Functor[G]): Aux[G[A], A => B, G[B]] =
		new Mapper[G[A], A => B]{
			type Out = G[B]

			def apply(ga: G[A], f: A => B) = functor.map(ga)(f)
		}
}