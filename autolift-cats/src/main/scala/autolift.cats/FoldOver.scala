package autolift.cats

import autolift.FoldOver
import export._

trait ScalazFoldOver[F[_], Obj] extends FoldOver[F, Obj]

@exports(Subclass)
object ScalazFoldOver extends LowPriorityScalazFoldOver{
	def apply[F[_], Obj](implicit fold: ScalazFoldOver[F, Obj]): Aux[F, Obj, fold.Out] = fold

	@export(Subclass)
	implicit def base[F[_], A](implicit fold: Foldable[F], m: Monoid[A]): Aux[F, F[A], A] =
		new ScalazFoldOver[F, F[A]]{
			type Out = A

			def apply(fa: F[A]) = fold.fold(fa)
		}
}

trait LowPriorityScalazFoldOver{
	type Aux[F[_], Obj, Out0] = ScalazFoldOver[F, Obj]{ type Out = Out0 }

	@export(Subclass)
	implicit def recur[F[_], G[_], H, Out0](implicit fold: Foldable[G], 
													 over: FoldOver.Aux[F, H, Out0], 
													 ev: Monoid[Out0]): Aux[F, G[H], Out0] =
		new ScalazFoldOver[F, G[H]]{
			type Out = Out0

			def apply(gh: G[H]) = fold.foldMap(gh){ h: H => over(h) }
		}
}