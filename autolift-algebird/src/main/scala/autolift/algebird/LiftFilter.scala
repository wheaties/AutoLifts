package autolift.algebird

import autolift.LiftFilter
import com.twitter.algebird.{Functor, Monoid, Monad}

trait AlgeLiftFilter[Obj, Fn] extends LiftFilter[Obj, Fn]

object AlgeLiftFilter extends LowPriorityAlgeLiftFilter{
	def apply[Obj, Fn](implicit lift: AlgeLiftFilter[Obj, Fn]) = lift

	implicit def base[M[_], A, B >: A](implicit fm: Monad[M], m: Monoid[M[A]]) =
		new AlgeLiftFilter[M[A], B => Boolean]{
			def apply(ma: M[A], pred: B => Boolean) = fm.flatMap(ma){ a: A => 
				if(pred(a)) fm(a) else m.zero
			}
		}
}

trait LowPriorityAlgeLiftFilter{

	implicit def recur[F[_], G, Fn](implicit lift: LiftFilter[G, Fn], functor: Functor[F]) =
		new AlgeLiftFilter[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

