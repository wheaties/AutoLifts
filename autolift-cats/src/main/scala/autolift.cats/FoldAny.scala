package autolift.cats

import autolift.FoldAny
import export._

trait ScalazFoldAny[Obj, Fn] extends FoldAny[Obj, Fn]

@exports(Subclass)
object ScalazFoldAny extends LowPriorityScalazFoldAny{
	def apply[Obj, Fn](implicit fold: ScalazFoldAny[Obj, Fn]) = fold

	@export(Subclass)
	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]) =
		new ScalazFoldAny[F[A], C => Boolean]{
			def apply(fa: F[A], f: C => Boolean) = fold.any(fa)(f)
		}
}

trait LowPriorityScalazFoldAny{

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit fold: Foldable[F], any: FoldAny[G, Fn]) =
		new ScalazFoldAny[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = fold.any(fg){ g: G => any(g, f) }
		}
}