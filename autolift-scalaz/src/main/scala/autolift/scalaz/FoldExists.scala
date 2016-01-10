package autolift.scalaz

import autolift.{FoldedExists, FoldExists}
import scalaz.Foldable
import export._

trait ScalazFoldExists[Obj, Fn] extends FoldExists[Obj, Fn]

@exports(Subclass)
object ScalazFoldExists extends LowPriorityScalazFoldExists {
  def apply[Obj, Fn](implicit fold: ScalazFoldExists[Obj, Fn]) = fold

  @export(Subclass)
  implicit def base[F[_], A, C >: A](implicit fold: Foldable[F]) =
    new ScalazFoldExists[F[A], C => Boolean]{
      def apply(fa: F[A], f: C => Boolean) = fold.any(fa)(f)
    }
}

trait LowPriorityScalazFoldExists {

  @export(Subclass)
  implicit def recur[F[_], G, Fn](implicit fold: Foldable[F], exists: FoldExists[G, Fn]) =
    new ScalazFoldExists[F[G], Fn]{
      def apply(fg: F[G], f: Fn) = fold.any(fg){ g: G => exists(g, f) }
    }
}

trait FoldAnySyntax {
  implicit class FoldAnyOps[F[_], A](fa: F[A]) {
    def foldAny[B](f: B => Boolean)(implicit fold: FoldExists[F[A], B => Boolean]) = fold(fa, f)
  }
}

trait FoldAnyContext {
  def foldAny[A](f: A => Boolean): FoldedAny[A] = new FoldedExists(f)

  type FoldedAny[A] = FoldedExists[A]
}
