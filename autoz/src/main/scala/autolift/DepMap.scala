package autolift

import scalaz._

trait DepMap[FA, -Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object DepMap extends DepMapLowPriority{
	def apply[FA, Function](implicit depmap: DepMap[FA, Function]): Aux[FA, Function, depmap.Out] = depmap

	implicit def fm[M[_], A, B](implicit bind: Bind[M]): Aux[M[A], A => M[B], M[B]] =
		new DepMap[M[A], A => M[B]]{
			type Out = M[B]

			def apply(ma: M[A], f: A => M[B]) = bind.bind(ma)(f)
		}
}

trait DepMapLowPriority{
	type Aux[FA, Function, Out0] = DepMap[FA, Function]{ type Out = Out0 }

	implicit def m[F[_], A, B](implicit functor: Functor[F]): Aux[F[A], A => B, F[B]] =
		new DepMap[F[A], A => B]{
			type Out = F[B]

			def apply(fa: F[A], f: A => B) = functor.map(fa)(f)
		}
}