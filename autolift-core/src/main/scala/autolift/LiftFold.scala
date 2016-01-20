package autolift


/**
 * Type class supporting folding over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 */
trait LiftFold[Obj] extends DFunction1[Obj]

trait LiftFoldSyntax{

	/// Syntax extension providing for a `liftFold` method.
	implicit class LiftFoldOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a Fold on the first nested type which has as a type parameter a Monoid.
		 */
		def liftFold(implicit lift: LiftFold[F[A]]): lift.Out = lift(fa)
	}
}

//Contexts for a liftFold do not exist.

