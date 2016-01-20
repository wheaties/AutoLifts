package autolift

/**
 * Type class supporting joining over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The type which will be lifted and joined into the left hand side
 * @tparam Obj2 The type which will be lifted and joined into the right hand side
 */
trait LiftJoin[Obj1, Obj2] extends DFunction2[Obj1, Obj2]

trait LiftJoinSyntax{

	/// Syntax extension providing for a `liftJoin` method.
	implicit class LiftJoinOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a `join` operation, type zipped dependent on the nested type structure.
		 *
		 * @param that the object to be joined.
		 * @tparam That the argument type of the object to be joined.
		 */
		def liftJoin[That](that: That)(implicit lift: LiftJoin[F[A], That]): lift.Out = lift(fa, that)
	}
}