package autolift

trait LiftJoinWith[Obj1, Obj2, Fn] extends DFunction3[Obj1, Obj2, Fn]

trait LiftJoinWithSyntax{

	/// Syntax extension providing for a `liftJoinWith` method.
	implicit class LiftJoinWithOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a `joing` operation based upon the application of a function.
		 *
		 * @param that the object to be joined.
		 * @param f the function over which to join
		 * @tparam That the type of the object to be joined
		 * @tparam B the first argument of the function used in the joining
		 * @tparam C the second argument of the function used in the joining
		 * @tparam D the return type of the function used in the joining
		 */
		def liftJoinWith[That, B, C, D](that: That)(f: (B, C) => D)(implicit lift: LiftJoinWith[F[A], That, (B, C) => D]): lift.Out = 
			lift(fa, that, f)
	}
}

final class LiftedJoinWith[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedJoinWith[A, B, D] = new LiftedJoinWith({ (a: A, b: B) => g(f(a, b)) })

	def apply[Obj1, Obj2](obj1: Obj1, obj2: Obj2)(implicit lift: LiftJoinWith[Obj1, Obj2, (A, B) => C]): lift.Out = 
		lift(obj1, obj2, f)
}

trait LiftJoinWithContext{
	def liftJoinWith[A, B, C](f: (A, B) => C) = new LiftedJoinWith(f)
}