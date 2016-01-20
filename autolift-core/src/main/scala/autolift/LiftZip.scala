package autolift

/**
 * Type class supporting zipping over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The type which will be lifted and zipped into the left hand side
 * @tparam Obj2 The type which will be lifted and zipped into the right hand side
 */
trait LiftZip[Obj1, Obj2] extends DFunction2[Obj1, Obj2]

trait LiftZipSyntax{

	/// Syntax extension providing for a `liftZip` method.
	implicit class LiftZipOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a `zip` operation, type zipped dependent on the nested type structure.
		 *
		 * @param that the object to be zipped.
		 * @tparam That the argument type of the object to be zipped.
		 */
		def liftZip[That](that: That)(implicit lift: LiftZip[F[A], That]): lift.Out = lift(fa, that)
	}
}