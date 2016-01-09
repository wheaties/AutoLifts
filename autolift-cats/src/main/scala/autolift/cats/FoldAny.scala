package autolift.cats

import cats.Foldable
import autolift.{FoldedAny, FoldAny}
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

trait FoldExistsSyntax {
  implicit class FoldExistsOps[F[_], A](fa: F[A]) {
    def foldExists[B](f: B => Boolean)(implicit fold: FoldAny[F[A], B => Boolean]) = fold(fa, f)
  }
}

trait FoldExistsContext {
  def foldExists[A](f: A => Boolean): FoldedExists[A] = new FoldedAny(f)

  type FoldedExists[A] = FoldedAny[A]
}
