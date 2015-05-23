package autolift

import scalaz.{Functor, Apply, Bind, Foldable, Monoid}

object Lifters extends Lifters

trait Lifters{
	def liftIntoF[F[_]] = new LIFMaker[F]

	sealed class LIFMaker[F[_]]{
		def apply[Function](f: Function)(implicit ev: Functor[F]) = new LiftIntoFunctor[F, Function](f)
	}

	def liftF[Function](f: Function) = new LiftedF(f)

	def liftAp[Function](f: Function) = new LiftedAp(f)

	def liftM[Function](f: Function) = new LiftedB(f)

	//TODO: Move to Folders.scala
	def foldOver[F[_]: Foldable] = new FoldOver[F]

	//def liftFoldMap[Function](f: Function) = new LiftedFoldMap(f)
}

class LiftIntoFunctor[F[_]: Functor, Function](f: Function){
	def apply[That](that: That)(implicit into: LiftIntoF[F, That, Function]): into.Out = into(that, f)
}

sealed trait LiftIntoF[F[_], Obj, Function] extends DFunction2[Obj, Function]

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

sealed trait LiftF[Obj, Function] extends DFunction2[Obj, Function]

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

sealed class LiftedAp[Function](f: Function){
	def apply[That](that: That)(implicit lift: LiftAp[That, Function]): lift.Out = lift(that, f)
}

sealed trait LiftAp[Obj, Function] extends DFunction2[Obj, Function]

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

sealed trait LiftB[Obj, Function] extends DFunction2[Obj, Function]

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

trait LiftFoldLeft[FA, Function, Z] extends DFunction3[FA, Function, Z]

object LiftFoldLeft extends LowPriorityLiftFoldLeft{
	def apply[FA, Function, Z](implicit lift: LiftFoldLeft[FA, Function, Z]): Aux[FA, Function, Z, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (B, C) => B, B, B] =
		new LiftFoldLeft[F[A], (B, C) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (B, C) => B, z: B) = fold.foldLeft(fa, z)(f)
		}
}

trait LowPriorityLiftFoldLeft{
	type Aux[FA, Function, Z, Out0] = LiftFoldLeft[FA, Function, Z]{ type Out = Out0 }

	implicit def recur[F[_], G, Function, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Function, Z]): Aux[F[G], Function, Z, F[lift.Out]] =
		new LiftFoldLeft[F[G], Function, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

trait LiftFoldRight[FA, Function, Z] extends DFunction3[FA, Function, Z]

object LiftFoldRight extends LowPriorityLiftFoldRight{
	def apply[FA, Function, Z](implicit lift: LiftFoldRight[FA, Function, Z]): Aux[FA, Function, Z, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (C, => B) => B, B, B] =
		new LiftFoldRight[F[A], (C, => B) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (C, => B) => B, z: B) = fold.foldRight(fa, z)(f)
		}
}

trait LowPriorityLiftFoldRight{
	type Aux[FA, Function, Z, Out0] = LiftFoldRight[FA, Function, Z]{ type Out = Out0 }

	implicit def recur[F[_], G, Function, Z](implicit functor: Functor[F], lift: LiftFoldRight[G, Function, Z]): Aux[F[G], Function, Z, F[lift.Out]] =
		new LiftFoldRight[F[G], Function, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

trait LiftFold[FA] extends DFunction1[FA]

object LiftFold extends LowPriorityLiftFold{
	def apply[FA](implicit lift: LiftFold[FA]): Aux[FA, lift.Out] = lift

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new LiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityLiftFold{
	type Aux[FA, Out0] = LiftFold[FA]{ type Out = Out0 }

	implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
		new LiftFold[F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

trait LiftFoldMap[FA, Function] extends DFunction2[FA, Function]

object LiftFoldMap extends LowPriorityLiftFoldMap{
	def apply[FA, Function](implicit lift: LiftFoldMap[FA, Function]): Aux[FA, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new LiftFoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityLiftFoldMap{
	type Aux[FA, Function, Out0] = LiftFoldMap[FA, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], lift: LiftFoldMap[G, Function]): Aux[F[G], Function, F[lift.Out]] =
		new LiftFoldMap[F[G], Function]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => lift(g, f) }
		}
}