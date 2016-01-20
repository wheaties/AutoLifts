package autolift


/**
 * Type class supporting fold over an arbitrary nesting of type constructors given a function which maps initial types to
 * some other type defined with a Monoid.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The function to be used to map values.
 */
trait LiftFoldMap[FA, Function] extends DFunction2[FA, Function]

trait LiftFoldMapSyntax{

	/// Syntax extension providing for a `liftFoldMap` method.
	implicit class LiftFoldMapOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a Fold dictated by the signature of a function and given that the mapping maps one type 
		 * to another which has a Monoid.
		 *
		 * @param f the function over which to fold.
		 * @tparam B the argument of the function.
		 * @tparam C the return type of the function which must have a Monoid.
		 */
		def liftFoldMap[B, C](f: B => C)(implicit lift: LiftFoldMap[F[A], B => C]): lift.Out = lift(fa, f)
	}
}

//See individual instances for liftFoldMap Context.

