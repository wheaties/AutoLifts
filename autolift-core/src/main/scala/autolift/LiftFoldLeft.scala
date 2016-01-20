package autolift


/**
 * Type class supporting foldLeft over an arbitrary nesting of type constructors given an initial value and a function.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The 2-airy function to be lifted.
 * @tparam Z The initial value of the fold.
 */
trait LiftFoldLeft[Obj, Function, Z] extends DFunction3[Obj, Function, Z]

object LiftFoldLeft

trait LiftFoldLeftSyntax{

	/// Syntax extension provided for a `liftFoldLeft` method.
	implicit class LiftFoldLeftOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a FoldL dicated by the argument type of the function.
		 *
		 * @param z the initial value which starts the fold.
		 * @param f the function which defines the fold.
		 * @tparam B the type to be folded.
		 * @tparam Z the resultant type of the fold.
		 */
		def liftFoldLeft[B, Z](z: Z)(f: (Z, B) => Z)(implicit lift: LiftFoldLeft[F[A], (Z, B) => Z, Z]): lift.Out = 
			lift(fa, f, z)
	}
}

final class LiftedFoldLeft[B, Z](z: Z, f: (Z, B) => Z){
	def apply[That](that: That)(implicit lift: LiftFoldLeft[That, (Z, B) => Z, Z]): lift.Out = lift(that, f, z)
}

trait LiftFoldLeftContext{
	def liftFoldLeft[B, Z](z: Z)(f: (Z, B) => Z) = new LiftedFoldLeft(z, f)
}

