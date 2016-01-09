package autolift.cats

import cats.Foldable
import autolift.FoldAny
import export._

trait CatsFoldAny[Obj, Fn] extends FoldAny[Obj, Fn]

@exports(Subclass)
object CatsFoldAny extends LowPriorityCatsFoldAny{
	def apply[Obj, Fn](implicit fold: CatsFoldAny[Obj, Fn]) = fold

	@export(Subclass)
	implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]) =
		new CatsFoldAny[F[A], C => Boolean]{
			def apply(fa: F[A], f: C => Boolean) = fold.exists(fa)(f)
		}
}

trait LowPriorityCatsFoldAny{

	@export(Subclass)
	implicit def recur[F[_], G, Fn](implicit fold: Foldable[F], any: FoldAny[G, Fn]) =
		new CatsFoldAny[F[G], Fn]{
			def apply(fg: F[G], f: Fn) = fold.exists(fg){ g: G => any(g, f) }
		}
}

//TODO: Come back to add more 'cats-like' syntax of `exists` vs `Any` as exposed in FoldAny
