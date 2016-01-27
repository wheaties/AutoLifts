package autolift

/**
 * Type class supporting merging over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The type which will be lifted and merged into the left hand side
 * @tparam Obj2 The type which will be lifted and merged into the right hand side
 */
trait LiftMerge[Obj1, Obj2] extends DFunction2[Obj1, Obj2]

trait LiftMergeSyntax{

	/// Syntax extension providing for a `liftMerge` method.
	implicit class LiftMergeOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a `merge` operation, type merged dependent on the nested type structure.
		 *
		 * @param that the object to be merged.
		 * @tparam That the argument type of the object to be merged.
		 */
		def liftMerge[That](that: That)(implicit lift: LiftMerge[F[A], That]): lift.Out = lift(fa, that)
	}
}