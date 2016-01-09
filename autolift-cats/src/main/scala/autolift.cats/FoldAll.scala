package autolift.cats

import autolift.FoldAll
import export._

trait ScalazFoldAll[Obj, Fn] extends FoldAll[Obj, Fn]

@exports(Subclass)
object ScalazFoldAll extends LowPriorityScalazFoldAll{
	def apply[Obj, Fn](implicit fold: ScalazFoldAll[Obj, Fn]) = fold

	@export(Subclass)
	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]) =
		new ScalazFoldAll[F[A], C => Boolean]{
			def apply(fa: F[A], f: C => Boolean) = fold.all(fa)(f)
		}
}

trait LowPriorityScalazFoldAll{

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit fold: Foldable[F], all: FoldAll[G, Fn]) =
		new ScalazFoldAll[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = fold.all(fg){ g: G => all(g, f) }
		}
}