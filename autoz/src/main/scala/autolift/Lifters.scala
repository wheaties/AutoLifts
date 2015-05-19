package autolift

import scalaz.{Functor, Apply, Bind, Foldable, Monoid}

//TODO: liftIntoAp?
//TODO: Think about map (compose) and if it's even possible to enforce compile time guarantees.

object Lifters extends Lifters

trait Lifters{
	def liftIntoF[F[_]] = new LIFMaker[F]

	sealed class LIFMaker[F[_]]{
		def apply[Function](f: Function)(implicit ev: Functor[F]) = new LiftIntoFunctor[F, Function](f)
	}

	def liftF[Function](f: Function) = new LiftedF(f)

	def liftAp[Function](f: Function) = new LiftedAp(f)

	def liftM[Function](f: Function) = new LiftedB(f)

	def foldOver[F[_]: Foldable] = new FoldOver[F]

	//should be foldMap
	def liftFoldMap[Function](f: Function) = new LiftedFoldMap(f)
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

sealed class LiftedFoldMap[Function](f: Function){
	def apply[That](that: That)(implicit fold: LiftFoldMap[That, Function]): fold.Out = fold(that, f)
}

trait LiftFoldMap[Obj, Function]{
	type Out

	def apply(obj: Obj, f: Function): Out
}

object LiftFoldMap extends LowPriorityLiftFoldMap{
	def apply[Obj, Function](implicit lift: LiftFoldMap[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new LiftFoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityLiftFoldMap{
	type Aux[Obj, Function, Out0] = LiftFoldMap[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function, Out0](implicit fold: Foldable[F], 
														 lift: LiftFoldMap.Aux[G, Function, Out0], 
														 ev: Monoid[Out0]): Aux[F[G], Function, Out0] =
		new LiftFoldMap[F[G], Function]{
			type Out = Out0

			def apply(fg: F[G], f: Function) = fold.foldMap(fg){ g: G => lift(g, f) }
		}
}

trait LiftFold[Obj]{
	type Out

	def apply(obj: Obj): Out
}

object LiftFold extends LowPriorityLiftFold{
	def apply[Obj](implicit lift: LiftFold[Obj]): Aux[Obj, lift.Out] = lift

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new LiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityLiftFold{
	type Aux[Obj, Out0] = LiftFold[Obj]{ type Out = Out0 }

	implicit def recur[F[_], G, Out0](implicit fold: Foldable[F], 
											   lift: LiftFold.Aux[G, Out0], 
											   ev: Monoid[Out0]): Aux[F[G], Out0] =
		new LiftFold[F[G]]{
			type Out = Out0

			def apply(fg: F[G]) = fold.foldMap(fg){ g: G => lift(g) }
		}
}

sealed class FoldOver[F[_]: Foldable]{
	def apply[That](that: That)(implicit fold: FoldedOver[F, That]): fold.Out = fold(that)
}

trait FoldedOver[F[_], Obj]{
	type Out

	def apply(obj: Obj): Out
}

object FoldedOver extends LowPriorityFoldedOver{
	def apply[F[_], Obj](implicit fold: FoldedOver[F, Obj]): Aux[F, Obj, fold.Out] = fold

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F, F[A], A] =
		new FoldedOver[F, F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityFoldedOver{
	type Aux[F[_], Obj, Out0] = FoldedOver[F, Obj]{ type Out = Out0 }

	implicit def recur[F[_], G[_], H, Out0](implicit fold: Foldable[G], 
													 over: FoldedOver.Aux[F, H, Out0], 
													 ev: Monoid[Out0]): Aux[F, G[H], Out0] =
		new FoldedOver[F, G[H]]{
			type Out = Out0

			def apply(gh: G[H]) = fold.foldMap(gh){ h: H => over(h) }
		}
}