package autolift.algebird

import autolift.DFunction3
import com.twitter.algebird.{Functor, Applicative}
import export._

trait LiftJoinWith[Obj1, Obj2, Fn] extends DFunction3[Obj1, Obj2, Fn]

@exports
object LiftJoinWith extends LowPriorityLiftJoinWith{
	def apply[Obj1, Obj2, Fn](implicit lift: LiftJoinWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	@export
	implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Applicative[F]): Aux[F[G], F[H], (G1,H1) => Out0, F[Out0]] =
		new LiftJoinWith[F[G], F[H], (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.joinWith(fg, fh)(f)
		}
}

@imports[LiftJoinWith]
trait LowPriorityLiftJoinWith{
	type Aux[Obj1, Obj2, Fn, Out0] = LiftJoinWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	@export
	implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftJoinWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
		new LiftJoinWith[F[G], H, Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
		}
}

trait LiftJoinWithSyntax{
	implicit class LiftJoinWithOps[F[_], A](fa: F[A]){
		def liftJoinWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftJoinWith[F[A], That, (B, C) => D]): lift.Out = 
			lift(fa, that, f)
	}
}

final class LiftedJoinWith[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedJoinWith[A, B, D] = new LiftedJoinWith({ (a: A, b: B) => g(f(a, b)) })

	def apply[Obj1, Obj2](obj1: Obj1, obj2: Obj2)(implicit lift: LiftJoinWith[Obj1, Obj2, (A, B) => C]): lift.Out = 
		lift(obj1, obj2, f)
}

trait LiftJoinWithContext{
	def liftJoinWith[A, B, C](f: (A, B) => C) = new LiftedJoinWith(f)
}