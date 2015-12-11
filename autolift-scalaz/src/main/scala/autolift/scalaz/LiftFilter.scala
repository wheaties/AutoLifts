package autolift.scalaz

import scalaz.{Functor, Foldable, MonadPlus, Monoid, Applicative}
import autolift.LiftFilter
import export._

trait ScalazLiftFilter[Obj, Fn] extends LiftFilter[Obj, Fn]

@exports(Subclass)
object ScalazLiftFilter extends LowPriorityScalazLiftFilter{
	def apply[Obj, Fn](implicit lift: LiftFilter[Obj, Fn]) = lift

	@export(Subclass)
	implicit def plus[M[_], A, B >: A](implicit mp: MonadPlus[M]) =
		new ScalazLiftFilter[M[A], B => Boolean]{
			def apply(ma: M[A], pred: B => Boolean) = mp.filter(ma)(pred)
		}
}

trait LowPriorityScalazLiftFilter extends LowPriorityScalazLiftFilter1{

	@export(Subclass)
	implicit def foldable[F[_], A, B >: A](implicit fold: Foldable[F], m: Monoid[F[A]], ap: Applicative[F]) =
		new ScalazLiftFilter[F[A], B => Boolean]{
			def apply(fa: F[A], pred: B => Boolean) = fold.foldRight(fa, m.zero){
				(a, res) => if(pred(a)) m.append(ap.pure(a), res) else res
			}
		}
}

trait LowPriorityScalazLiftFilter1{

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit lift: LiftFilter[G, Fn], functor: Functor[F]) =
		new ScalazLiftFilter[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}