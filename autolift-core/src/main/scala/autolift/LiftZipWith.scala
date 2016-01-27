package autolift

/**
 * Typeclass supporting the zipping and then application of a function on the zipped results over an arbitrary nesting 
 * of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The type which will be lifted and zipped into the left hand side
 * @tparam Obj2 The type which will be lifted and zipped into the right hand side
 * @tparam Fn The function which will be lifted and applied to the results
 */
trait LiftZipWith[Obj1, Obj2, Fn] extends DFunction3[Obj1, Obj2, Fn]

trait LiftZipWithSyntax{

	/// Syntax extension providing for a `liftZipWith` method.
	implicit class LiftZipWithOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a `zip` operation based upon the application of a function.
		 *
		 * @param that the object to be zipped.
		 * @param f the function over which to zip
		 * @tparam That the type of the object to be zipped
		 * @tparam B the first argument of the function used in the zipping
		 * @tparam C the second argument of the function used in the zipping
		 * @tparam D the return type of the function used in the zipping
		 */
		def liftZipWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftZipWith[F[A], That, (B, C) => D]): lift.Out = 
			lift(fa, that, f)
	}
}

final class LiftedZipWith[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedZipWith[A, B, D] = new LiftedZipWith({ (a: A, b: B) => g(f(a, b)) })

	def apply[Obj1, Obj2](obj1: Obj1, obj2: Obj2)(implicit lift: LiftZipWith[Obj1, Obj2, (A, B) => C]): lift.Out = 
		lift(obj1, obj2, f)
}

trait LiftZipWithContext{
	def liftZipWith[A, B, C](f: (A, B) => C) = new LiftedZipWith(f)
}