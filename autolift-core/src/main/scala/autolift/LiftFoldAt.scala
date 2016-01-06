package autolift

import export._

/**
 * Type class supporting folding over a nested type constructor up to and including a type constructor.
 *
 * @author Owein Reese
 *
 * @tparam F The type at which to stop folding.
 * @tparam Obj The type over which to lift the folding.
 */
trait LiftFoldAt[F[_], Obj] extends DFunction1[Obj]

@imports[LiftFoldAt]
object LiftFoldAt

trait LiftFoldAtSyntax{

	/// Syntax extension providing for a `liftFoldAt` method.
	implicit class LiftFoldAtOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a Fold at the indicated type, assuming the type permits folding and the contained type has 
		 * a Monoid.
		 * 
		 * @tparam M the higher-kinded type for which there is the notion of folding or traversing.
		 */
		def liftFoldAt[M[_]](implicit fold: LiftFoldAt[M, F[A]]): fold.Out = fold(fa)
	}
}

//There does not exist Context for liftFoldAt.