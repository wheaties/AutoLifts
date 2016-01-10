package autolift.cats

import cats.Foldable
import autolift.{FoldedExists, FoldExists}
import export._

trait CatsFoldExists[Obj, Fn] extends FoldExists[Obj, Fn]

@exports(Subclass)
object CatsFoldExists extends LowPriorityCatsFoldExists{
  def apply[Obj, Fn](implicit fold: CatsFoldExists[Obj, Fn]) = fold

  @export(Subclass)
  implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]) =
    new CatsFoldExists[F[A], C => Boolean]{
      def apply(fa: F[A], f: C => Boolean) = fold.exists(fa)(f)
    }
}

trait LowPriorityCatsFoldExists{

  @export(Subclass)
  implicit def recur[F[_], G, Fn](implicit fold: Foldable[F], exists: FoldExists[G, Fn]) =
    new CatsFoldExists[F[G], Fn]{
      def apply(fg: F[G], f: Fn) = fold.exists(fg){ g: G => exists(g, f) }
    }
}

