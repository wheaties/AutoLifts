package autolift

import scalaz.{Bind, Apply, Functor}

trait Mappers{
	def auto[Function](f: Function) = new AutoApply(f)

	sealed class AutoApply[Function](f: Function){
		def apply[FA](fa: FA)(implicit dm: DepMap[FA, Function]): dm.Out = dm(fa, f)
	}
}

trait DepMap[FA, -Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object DepMap extends DepMapLowerPriority{
	def apply[FA, Function](implicit depmap: DepMap[FA, Function]): Aux[FA, Function, depmap.Out] = depmap

	implicit def fm[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M[A], C => M[B], M[B]] =
		new DepMap[M[A], C => M[B]]{
			type Out = M[B]

			def apply(ma: M[A], f: C => M[B]) = bind.bind(ma)(f)
		}
}

trait DepMapLowerPriority extends DepMapLowPriority{
	implicit def ap[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new DepMap[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}

trait DepMapLowPriority{
	type Aux[FA, Function, Out0] = DepMap[FA, Function]{ type Out = Out0 }

	implicit def m[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new DepMap[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}