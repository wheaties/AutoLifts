package autolift

import scalaz.{Functor, Apply, Bind}

//TODO: liftIntoAp?
//TODO: Think about map (compose) and if it's even possible to enforce compile time guarantees.

object Lifters extends Lifters

trait Lifters{
	def liftIntoF[F[_]] = new LIFMaker[F]

	sealed class LIFMaker[F[_]]{
		def apply[Function](f: Function)(implicit ev: Functor[F]) = new LiftIntoFunctor[F, Function](f)
	}

	def liftIntoM[M[_]] = new LIBMaker[M]

	sealed class LIBMaker[F[_]]{
		def apply[Function](f: Function)(implicit ev: Bind[F]) = new LiftIntoBind[F, Function](f)
	}

	def liftF[Function](f: Function) = new LiftedF(f)

	def liftAp[Function](f: Function) = new LiftedAp(f)

	def liftM[Function](f: Function) = new LiftedB(f)
}

class LiftIntoFunctor[F[_]: Functor, Function](f: Function){
	def apply[That](that: That)(implicit into: LiftIntoF[F, That, Function]): into.Out = into(that, f)
}

sealed trait LiftIntoF[F[_], Obj, Function]{
	type Out

	def apply(obj: Obj, f: Function): Out
}

object LiftIntoF extends LowPriorityLiftIntoF{
	def apply[F[_], Obj, Function](implicit lift: LiftIntoF[F, Obj, Function]): Aux[F, Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F, F[A], C => B, F[B]] =
		new LiftIntoF[F, F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityLiftIntoF{
	type Aux[F[_], Obj, Function, Out0] = LiftIntoF[F, Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G[_], In, Function](implicit functor: Functor[G], lift: LiftIntoF[F, In, Function]): Aux[F, G[In], Function, G[lift.Out]] =
		new LiftIntoF[F, G[In], Function]{
			type Out = G[lift.Out]

			def apply(gin: G[In], f: Function) = functor.map(gin){ in: In => lift(in, f) }
		}
}

sealed class LiftIntoBind[M[_]: Bind, Function](f: Function){
	def apply[That](that: That)(implicit into: LiftIntoB[M, That, Function]): into.Out = into(that, f)
}

sealed trait LiftIntoB[M[_], Obj, Function]{
	type Out

	def apply(obj: Obj, f: Function): Out
}

object LiftIntoB extends LowPriorityLiftIntoB{
	def apply[M[_], Obj, Function](implicit lift: LiftIntoB[M, Obj, Function]): Aux[M, Obj, Function, lift.Out] = lift

	implicit def base[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M, M[A], C => M[B], M[B]] =
		new LiftIntoB[M, M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = bind.bind(fa)(f)
		}
}

trait LowPriorityLiftIntoB{
	type Aux[F[_], Obj, Function, Out0] = LiftIntoB[F, Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G[_], In, Function](implicit functor: Functor[G], lift: LiftIntoB[F, In, Function]): Aux[F, G[In], Function, G[lift.Out]] =
		new LiftIntoB[F, G[In], Function]{
			type Out = G[lift.Out]

			def apply(gin: G[In], f: Function) = functor.map(gin){ in: In => lift(in, f) }
		}
}

sealed class LiftedF[Function](f: Function){
	def apply[That](that: That)(implicit lift: LiftF[That, Function]): lift.Out = lift(that, f)
}

sealed trait LiftF[Obj, Function]{
	type Out

	def apply(obj: Obj, f: Function): Out
}

object LiftF extends LowPriorityLiftF {
	def apply[Obj, Function](implicit lift: LiftF[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new LiftF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityLiftF{
	type Aux[Obj, Function, Out0] = LiftF[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftF[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftF[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

//
sealed class LiftedAp[Function](f: Function){
	def apply[That](that: That)(implicit lift: LiftAp[That, Function]): lift.Out = lift(that, f)
}

sealed trait LiftAp[Obj, Function]{
	type Out

	def apply(obj: Obj, f: Function): Out
}

object LiftAp extends LowPriorityLiftAp {
	def apply[Obj, Function](implicit lift: LiftAp[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new LiftAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}

trait LowPriorityLiftAp{
	type Aux[Obj, Function, Out0] = LiftAp[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftAp[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftAp[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}

sealed class LiftedB[Function](f: Function){
	def apply[That](that: That)(implicit lift: LiftB[That, Function]): lift.Out = lift(that, f)
}

sealed trait LiftB[Obj, Function]{
	type Out

	def apply(obj: Obj, f: Function): Out
}

object LiftB extends LowPriorityLiftB {
	def apply[Obj, Function](implicit lift: LiftB[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M[A], C => M[B], M[B]] =
		new LiftB[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = bind.bind(fa)(f)
		}
}

trait LowPriorityLiftB{
	type Aux[Obj, Function, Out0] = LiftB[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftB[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftB[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}