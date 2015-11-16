package autolift.algebird

import autolift._
import com.twitter.algebird._
import export._

trait AlgeLiftF[Obj, Fn] extends LiftF[Obj, Fn]

@exports(Subclass)
object AlgeLiftF extends LowPriorityAlgeLiftF{
	def apply[Obj, Fn](implicit lift: AlgeLiftF[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new AlgeLiftF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityAlgeLiftF{
	type Aux[Obj, Fn, Out0] = AlgeLiftF[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftF[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new AlgeLiftF[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait AlgeLiftAp[Obj, Fn] extends LiftAp[Obj, Fn]

@exports(Subclass)
object AlgeLiftAp extends LowPriorityAlgeLiftAp {
	def apply[Obj, Fn](implicit lift: AlgeLiftAp[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, B](implicit ap: Applicative[F]): Aux[F[A], F[A => B], F[B]] =
		new AlgeLiftAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], ff: F[A => B]) = ap.joinWith(fa, ff){ (a, f) => f(a) }
		}
}

trait LowPriorityAlgeLiftAp{
	type Aux[Obj, Fn, Out0] = AlgeLiftAp[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftAp[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new AlgeLiftAp[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait AlgeLiftB[Obj, Fn] extends LiftB[Obj, Fn]

@exports(Subclass)
object AlgeLiftB extends LowPriorityAlgeLiftB {
	def apply[Obj, Fn](implicit lift: AlgeLiftB[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A, C >: A, B](implicit fm: Monad[M]): Aux[M[A], C => M[B], M[B]] =
		new AlgeLiftB[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = fm.flatMap(fa)(f)
		}
}

trait LowPriorityAlgeLiftB{
	type Aux[Obj, Fn, Out0] = AlgeLiftB[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftB[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new AlgeLiftB[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait AlgeLiftFilter[Obj, Fn] extends LiftFilter[Obj, Fn]

@exports(Subclass)
object AlgeLiftFilter extends LowPriorityAlgeLiftFilter{
	def apply[Obj, Fn](implicit lift: AlgeLiftFilter[Obj, Fn]) = lift

	@export(Subclass)
	implicit def plus[M[_], A, B >: A](implicit fm: Monad[M], m: Monoid[M[A]]) =
		new AlgeLiftFilter[M[A], B => Boolean]{
			def apply(ma: M[A], pred: B => Boolean) = fm.flatMap(ma){ a: A => 
				if(pred(a)) fm(a) else m.zero
			}
		}
}

trait LowPriorityAlgeLiftFilter{

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit lift: LiftFilter[G, Fn], functor: Functor[F]) =
		new AlgeLiftFilter[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait AlgeLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

@exports(Subclass)
object AlgeLiftFlatten extends LowPriorityAlgeLiftFlatten{
	def apply[M[_], Obj](implicit lift: AlgeLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A](implicit fm: Monad[M]): Aux[M, M[M[A]], M[A]] =
		new AlgeLiftFlatten[M, M[M[A]]]{
			type Out = M[A]

			def apply(mma: M[M[A]]) = fm.flatMap(mma){ ma: M[A] => ma }
		}
}

trait LowPriorityAlgeLiftFlatten{
	type Aux[M[_], Obj, Out0] = AlgeLiftFlatten[M, Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
		new AlgeLiftFlatten[M, F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

trait LiftJoinWith[Obj1, Obj2, Fn] extends DFunction3[Obj1, Obj2, Fn]

@imports[LiftJoinWith]
object LiftJoinWith extends LowPriorityLiftJoinWith{
	def apply[Obj1, Obj2, Fn](implicit lift: LiftJoinWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

	implicit def base[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit ap: Applicative[F]): Aux[F[G], F[H], (G1,H1) => Out0, F[Out0]] =
		new LiftJoinWith[F[G], F[H], (G1, H1) => Out0]{
			type Out = F[Out0]

			def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = ap.joinWith(fg, fh)(f)
		}
}

trait LowPriorityLiftJoinWith{
	type Aux[Obj1, Obj2, Fn, Out0] = LiftJoinWith[Obj1, Obj2, Fn]{ type Out = Out0 }

	implicit def recur[F[_], G, H, Fn](implicit ap: Applicative[F], lift: LiftJoinWith[G, H, Fn]): Aux[F[G], F[H], Fn, F[lift.Out]] =
		new LiftJoinWith[F[G], F[H], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], fh: F[H], f: Fn) = ap.joinWith(fg, fh){ (g: G, h: H) => lift(g, h, f) }
		}
}

trait LiftJoinWithSyntax{
	implicit class LiftJoinWithOps[F[_], A](fa: F[A]){
		def liftJoinWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftJoinWith[F[A], That, (B, C) => D]): lift.Out = 
			lift(fa, that, f)
	}
}