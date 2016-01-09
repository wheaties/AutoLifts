package autolift.cats

import cats.Foldable
import autolift.FoldAll
import export._

trait CatsFoldAll[Obj, Fn] extends FoldAll[Obj, Fn]

@exports(Subclass)
object CatsFoldAll extends LowPriorityCatsFoldAll{
	def apply[Obj, Fn](implicit fold: CatsFoldAll[Obj, Fn]) = fold

	@export(Subclass)
	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]) =
		new CatsFoldAll[F[A], C => Boolean]{
			def apply(fa: F[A], f: C => Boolean) = fold.forall(fa)(f)
		}
}

trait LowPriorityCatsFoldAll{

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit fold: Foldable[F], all: FoldAll[G, Fn]) =
		new CatsFoldAll[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = fold.forall(fg){ g: G => all(g, f) }
		}
}

//TODO: Come back to add more 'cats-like' syntax of `forall` vs `all` as exposed in FoldAll
