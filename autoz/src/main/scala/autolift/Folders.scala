package autolift

import scalaz.{Functor, Apply, Bind, Foldable, Monoid}

trait Folders{
	def foldOver[F[_]: Foldable] = new FoldOver[F]
}

sealed class FoldedMap[Function](f: Function){
	def apply[That](that: That)(implicit fold: FoldMap[That, Function]): fold.Out = fold(that, f)
}

trait FoldMap[Obj, Function] extends DFunction2[Obj, Function]

object FoldMap extends LowPriorityFoldMap{
	def apply[Obj, Function](implicit lift: FoldMap[Obj, Function]): Aux[Obj, Function, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F], ev: Monoid[B]): Aux[F[A], C => B, B] =
		new FoldMap[F[A], C => B]{
			type Out = B

			def apply(fa: F[A], f: C => B) = fold.foldMap(fa)(f)
		}
}

trait LowPriorityFoldMap{
	type Aux[Obj, Function, Out0] = FoldMap[Obj, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function, Out0](implicit fold: Foldable[F], 
														 lift: FoldMap.Aux[G, Function, Out0], 
														 ev: Monoid[Out0]): Aux[F[G], Function, Out0] =
		new FoldMap[F[G], Function]{
			type Out = Out0

			def apply(fg: F[G], f: Function) = fold.foldMap(fg){ g: G => lift(g, f) }
		}
}

trait FoldAll[Obj] extends DFunction1[Obj]

object FoldAll extends LowPriorityFoldAll{
	def apply[Obj](implicit lift: FoldAll[Obj]): Aux[Obj, lift.Out] = lift

	implicit def base[F[_], A](implicit fold: Foldable[F], ev: Monoid[A]): Aux[F[A], A] =
		new FoldAll[F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityFoldAll{
	type Aux[Obj, Out0] = FoldAll[Obj]{ type Out = Out0 }

	implicit def recur[F[_], G, Out0](implicit fold: Foldable[F], 
											   lift: FoldAll.Aux[G, Out0], 
											   ev: Monoid[Out0]): Aux[F[G], Out0] =
		new FoldAll[F[G]]{
			type Out = Out0

			def apply(fg: F[G]) = fold.foldMap(fg){ g: G => lift(g) }
		}
}

sealed class FoldOver[F[_]: Foldable]{
	def apply[That](that: That)(implicit fold: FoldedOver[F, That]): fold.Out = fold(that)
}

trait FoldedOver[F[_], Obj] extends DFunction1[Obj]

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

