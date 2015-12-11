package autolift

import export._

/**
 * Type class supporting checking if all of some type defined by `Fn` evaluate to `true` within a nested stack of type
 * constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type constructors over which to evaluate `Fn`
 * @tparam Fn The boolean producing predicate which will be iterated over the first applicable type with type stack.
 */
trait FoldAll[Obj, Fn] extends ((Obj, Fn) => Boolean)

@imports[FoldAll]
object FoldAll

trait FoldAllSyntax{
	implicit class FoldAllOps[F[_], A](fa: F[A]){
		def foldAll[B](f: B => Boolean)(implicit fold: FoldAll[F[A], B => Boolean]): Boolean = fold(fa, f)
	}
}

final class FoldedAll[A](f: A => Boolean){
	def apply[That](that: That)(implicit fold: FoldAll[That, A => Boolean]): Boolean = fold(that, f)
}

trait FoldAllContext{
	def foldAll[A](f: A => Boolean) = new FoldedAll(f)
}