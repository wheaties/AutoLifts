package autolift

/**
 * Type class supporting lifting a sequence operation over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam F The type at which to sequence.
 * @tparam Obj The type over which to lift the sequencing.
 */
trait LiftSequence[F[_], Obj] extends DFunction1[Obj]

trait LiftSequenceSyntax{

	/// Syntax extension providing for a `liftSequence` method.
	implicit class LiftSequenceOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a sequence on the first nested type which has as a type parameter an Applicative.
		 */
		def liftSequence[M[_]](implicit lift: LiftSequence[M, F[A]]): lift.Out = lift(fa)
	}
}

//Contexts for a liftSequence do not exist.