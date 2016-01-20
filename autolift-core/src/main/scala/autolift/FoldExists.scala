package autolift


/**
 * Type class supporting checking if at least one of a type defined by `Function` evaluate to `true` within a nested stack of 
 * type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type constructors over which to evaluate `Function`
 * @tparam Function The boolean producing function which will be iterated over the first applicable type with type stack.
 */
trait FoldExists[Obj, Function] extends ((Obj, Function) => Boolean)

trait FoldExistsSyntax{
  implicit class FoldExistsOps[F[_], A](fa: F[A]){
    def foldExists[B](f: B => Boolean)(implicit fold: FoldExists[F[A], B => Boolean]): Boolean = fold(fa, f)
  }
}

final class FoldedExists[A](f: A => Boolean){
  def apply[That](that: That)(implicit fold: FoldExists[That, A => Boolean]): Boolean = fold(that, f)
}

trait FoldExistsContext{
  def foldExists[A](f: A => Boolean) = new FoldedExists(f)
}

