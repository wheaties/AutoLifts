package autolift.cats

import cats.Foldable
import autolift.{FoldedAll, FoldAll}
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

trait FoldForallSyntax {
  implicit class FoldForallOps[F[_], A](fa: F[A]) {
    def foldForall[B](f: B => Boolean)(implicit fold: FoldAll[F[A], B => Boolean]) = fold(fa, f)
  }
}

trait FoldForallContext {
  def foldForall[A](f: A => Boolean): FoldedForall[A] = new FoldedAll(f)

  type FoldedForall[A] = FoldedAll[A]
}
