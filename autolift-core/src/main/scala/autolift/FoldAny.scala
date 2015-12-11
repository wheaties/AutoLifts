package autolift

import export._

/**
 * Type class supporting checking if at least one of a type defined by `Function` evaluate to `true` within a nested stack of 
 * type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type constructors over which to evaluate `Function`
 * @tparam Function The boolean producing function which will be iterated over the first applicable type with type stack.
 */
trait FoldAny[Obj, Function] extends ((Obj, Function) => Boolean)

@imports[FoldAny]
object FoldAny

trait FoldAnySyntax{
	implicit class FoldAnyOps[F[_], A](fa: F[A]){
		def foldAny[B](f: B => Boolean)(implicit fold: FoldAny[F[A], B => Boolean]): Boolean = fold(fa, f)
	}
}

final class FoldedAny[A](f: A => Boolean){
	def apply[That](that: That)(implicit fold: FoldAny[That, A => Boolean]): Boolean = fold(that, f)
}

trait FoldAnyContext{
	def foldAny[A](f: A => Boolean) = new FoldedAny(f)
}