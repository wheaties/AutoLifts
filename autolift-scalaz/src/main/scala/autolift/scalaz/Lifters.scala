package autolift.scalaz

import scalaz._
import autolift._
import export._

trait ScalazLiftF[Obj, Fn] extends LiftF[Obj, Fn]

@exports(Subclass)
object ScalazLiftF extends LowPriorityScalazLiftF {
	def apply[Obj, Fn](implicit lift: ScalazLiftF[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new ScalazLiftF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityScalazLiftF{
	type Aux[Obj, Fn, Out0] = ScalazLiftF[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftF[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftF[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait ScalazLiftAp[Obj, Fn] extends LiftAp[Obj, Fn]

@exports(Subclass)
object ScalazLiftAp extends LowPriorityScalazLiftAp {
	def apply[Obj, Fn](implicit lift: ScalazLiftAp[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new ScalazLiftAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}

trait LowPriorityScalazLiftAp{
	type Aux[Obj, Fn, Out0] = ScalazLiftAp[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftAp[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftAp[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait ScalazLiftB[Obj, Fn] extends LiftB[Obj, Fn]

@exports(Subclass)
object ScalazLiftB extends LowPriorityScalazLiftB {
	def apply[Obj, Fn](implicit lift: ScalazLiftB[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A, C >: A, B](implicit bind: Bind[M]): Aux[M[A], C => M[B], M[B]] =
		new ScalazLiftB[M[A], C => M[B]]{
			type Out = M[B]

			def apply(fa: M[A], f: C => M[B]) = bind.bind(fa)(f)
		}
}

trait LowPriorityScalazLiftB{
	type Aux[Obj, Fn, Out0] = ScalazLiftB[Obj, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftB[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftB[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait ScalazLiftFoldLeft[Obj, Fn, Z] extends LiftFoldLeft[Obj, Fn, Z]

@exports(Subclass)
object ScalazLiftFoldLeft extends LowPriorityScalazLiftFoldLeft{
	def apply[FA, Fn, Z](implicit lift: ScalazLiftFoldLeft[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (B, C) => B, B, B] =
		new ScalazLiftFoldLeft[F[A], (B, C) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (B, C) => B, z: B) = fold.foldLeft(fa, z)(f)
		}
}

trait LowPriorityScalazLiftFoldLeft{
	type Aux[FA, Fn, Z, Out0] = ScalazLiftFoldLeft[FA, Fn, Z]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
		new ScalazLiftFoldLeft[F[G], Fn, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

trait ScalazLiftFoldRight[FA, Fn, Z] extends LiftFoldRight[FA, Fn, Z]

@exports(Subclass)
object ScalazLiftFoldRight extends LowPriorityScalazLiftFoldRight{
	def apply[FA, Fn, Z](implicit lift: ScalazLiftFoldRight[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (C, => B) => B, B, B] =
		new ScalazLiftFoldRight[F[A], (C, => B) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (C, => B) => B, z: B) = fold.foldRight(fa, z)(f)
		}
}

trait LowPriorityScalazLiftFoldRight{
	type Aux[FA, Fn, Z, Out0] = ScalazLiftFoldRight[FA, Fn, Z]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldRight[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
		new ScalazLiftFoldRight[F[G], Fn, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

trait ScalazLiftFold[Obj] extends LiftFold[Obj]

@exports(Subclass)
object ScalazLiftFold extends LowPriorityScalazLiftFold{
	def apply[FA](implicit lift: ScalazLiftFold[FA]): Aux[FA, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new ScalazLiftFold[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazLiftFold{
	type Aux[FA, Out0] = ScalazLiftFold[FA]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G](implicit functor: Functor[F], lift: LiftFold[G]): Aux[F[G], F[lift.Out]] =
		new ScalazLiftFold[F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

trait ScalazLiftFoldMap[FA, Fn] extends LiftFoldMap[FA, Fn]

@exports(Subclass)
object ScalazLiftFoldMap extends LowPriorityScalazLiftFoldMap{
	def apply[FA, Fn](implicit lift: ScalazLiftFoldMap[FA, Fn]): Aux[FA, Fn, lift.Out] = lift

	@export(Subclass)
	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new ScalazLiftFoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityScalazLiftFoldMap{
	type Aux[FA, Fn, Out0] = ScalazLiftFoldMap[FA, Fn]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftFoldMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
		new ScalazLiftFoldMap[F[G], Fn]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
		}
}

trait ScalazLiftFoldAt[F[_], Obj] extends LiftFoldAt[F, Obj]

@exports(Subclass)
object ScalazLiftFoldAt extends LowPriorityScalazLiftFoldAt{
	def apply[F[_], Obj](implicit fold: ScalazLiftFoldAt[F, Obj]): Aux[F, Obj, fold.Out] = fold

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
		new ScalazLiftFoldAt[F, F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazLiftFoldAt{
	type Aux[F[_], Obj, Out0] = ScalazLiftFoldAt[F, Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G[_], H](implicit functor: Functor[G], fold: LiftFoldAt[F, H]): Aux[F, G[H], G[fold.Out]] =
		new ScalazLiftFoldAt[F, G[H]]{
			type Out = G[fold.Out]

			def apply(gh: G[H]) = functor.map(gh){ h: H => fold(h) }
		}
}

trait ScalazLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

@exports(Subclass)
object ScalazLiftFlatten extends LowPriorityScalazLiftFlatten{
	def apply[M[_], Obj](implicit lift: ScalazLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

	@export(Subclass)
	implicit def base[M[_], A](implicit bind: Bind[M]): Aux[M, M[M[A]], M[A]] =
		new ScalazLiftFlatten[M, M[M[A]]]{
			type Out = M[A]

			def apply(mma: M[M[A]]) = bind.bind(mma){ ma: M[A] => ma }
		}
}

trait LowPriorityScalazLiftFlatten{
	type Aux[M[_], Obj, Out0] = ScalazLiftFlatten[M, Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
		new ScalazLiftFlatten[M, F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

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