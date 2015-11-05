package autolift.algebird

import autolift.LiftF
import com.twitter.algebird._
import export._

//TODO: put somewhere else
//@reexports[AlgeLiftF]
object Algebird

trait AlgeLiftF[Obj, Function] extends LiftF[Obj, Function]

@exports[AlgeLiftF]
object AlgeLiftF extends LowPriorityAlgeLiftF{
	def apply[Obj, Function](implicit lift: AlgeLiftF[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new AlgeLiftF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityAlgeLiftF{
	type Aux[Obj, Function, Out0] = AlgeLiftF[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftF[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new AlgeLiftF[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}