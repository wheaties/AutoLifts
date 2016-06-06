package autolift


/**
 * Type class supporting traversing a function over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The function to be lifted.
 */
trait LiftTraverse[Obj, Function] extends DFunction2[Obj, Function]

trait LiftTraverseSyntax{

	/// Syntax extension providing for a `liftTraverse` method.
	implicit class LiftTraverseOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting and traversing of the contained function `f` such that the application point is dicated by the
		 * argument and return type of the function.
		 *
		 * @param f the function that returns a type with an Applicative.
		 * @tparam B the argument type of the function.
		 * @tparam C the inner type of the return type of the function.
		 * @tparam M the higher-kinded type of the return type of the function which has an Applicative.
		 */
		def liftTraverse[B, C, M[_]](f: B => M[C])(implicit lift: LiftTraverse[F[A], B => M[C]]): lift.Out = lift(fa, f)
	}
}

//See individual implementations for liftTraverse Context.

