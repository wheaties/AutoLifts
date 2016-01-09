package autolift.cats

import autolift.FoldOver
import export._

trait CatsFoldOver[F[_], Obj] extends FoldOver[F, Obj]

@exports(Subclass)
object CatsFoldOver extends LowPriorityCatsFoldOver{
	def apply[F[_], Obj](implicit fold: CatsFoldOver[F, Obj]): Aux[F, Obj, fold.Out] = fold

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
		new CatsFoldOver[F, F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityCatsFoldOver{
	type Aux[F[_], Obj, Out0] = CatsFoldOver[F, Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G[_], H, Out0](implicit fold: Foldable[G], 
													 over: FoldOver.Aux[F, H, Out0], 
													 ev: Monoid[Out0]): Aux[F, G[H], Out0] =
		new CatsFoldOver[F, G[H]]{
			type Out = Out0

			def apply(gh: G[H]) = fold.foldMap(gh){ h: H => over(h) }
		}
}
