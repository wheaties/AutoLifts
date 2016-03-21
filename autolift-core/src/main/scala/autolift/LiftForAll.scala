package autolift

trait LiftForAll[Obj, Fn] extends DFunction2[Obj, Fn]

trait LiftForAllSyntax{

	/// Syntax extension providing for a `liftForAll` method.
	implicit class LiftForAllOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of the predicate `f` over the object such that the application point is dictated by the type
		 * of predicate invocation.
		 *
		 * @param f the predicate to be lifted.
		 * @tparam B the argument type of the predicate.
		 */
		def liftForAll[B](f: B => Boolean)(implicit lift: LiftForAll[F[A], B => Boolean]): lift.Out = lift(fa, f)
	}
}

final class LiftedForAll[A](f: A => Boolean){
	def apply[That](that: That)(implicit lift: LiftForAll[That, A => Boolean]): lift.Out = lift(that, f)
}

trait LiftForAllContext{
	def liftForAll[A](f: A => Boolean) = new LiftedForAll(f)
}

