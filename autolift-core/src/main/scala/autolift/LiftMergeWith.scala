package autolift

/**
 * Typeclass supporting the merging and then application of a function on the merged results over an arbitrary nesting 
 * of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The type which will be lifted and merged into the left hand side
 * @tparam Obj2 The type which will be lifted and merged into the right hand side
 * @tparam Fn The function which will be lifted and applied to the results
 */
trait LiftMergeWith[Obj1, Obj2, Fn] extends DFunction3[Obj1, Obj2, Fn]

trait LiftMergeWithSyntax{

	/// Syntax extension providing for a `liftMergeWith` method.
	implicit class LiftMergeWithOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a `merging` operation based upon the application of a function.
		 *
		 * @param that the object to be merged
		 * @param f the function over which to merge
		 * @tparam That the type of the object to be merged
		 * @tparam B the first argument of the function used in the merging
		 * @tparam C the second argument of the function used in the merging
		 * @tparam D the return type of the function used in the merging
		 */
		def liftMergeWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftMergeWith[F[A], That, (B, C) => D]): lift.Out = 
			lift(fa, that, f)
	}
}

final class LiftedMergeWith[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedMergeWith[A, B, D] = new LiftedMergeWith({ (a: A, b: B) => g(f(a, b)) })

	def apply[Obj1, Obj2](obj1: Obj1, obj2: Obj2)(implicit lift: LiftMergeWith[Obj1, Obj2, (A, B) => C]): lift.Out = 
		lift(obj1, obj2, f)
}

trait LiftMergeWithContext{
	def liftMergeWith[A, B, C](f: (A, B) => C) = new LiftedMergeWith(f)
}