package autolift.scalaz

import scalaz.{Functor, MonadPlus}
import autolift.LiftFilter

trait ScalazLiftFilter[Obj, Fn] extends LiftFilter[Obj, Fn]

object ScalazLiftFilter extends LowPriorityScalazLiftFilter{
	def apply[Obj, Fn](implicit lift: LiftFilter[Obj, Fn]) = lift

	implicit def plus[M[_], A, B >: A](implicit mp: MonadPlus[M]) =
		new ScalazLiftFilter[M[A], B => Boolean]{
			def apply(ma: M[A], pred: B => Boolean) = mp.filter(ma)(pred)
		}
}

trait LowPriorityScalazLiftFilter{

	implicit def recur[F[_], G, Fn](implicit lift: LiftFilter[G, Fn], functor: Functor[F]) =
		new ScalazLiftFilter[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

