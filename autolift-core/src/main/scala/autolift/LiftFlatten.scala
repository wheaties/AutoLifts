package autolift

import export._

/**
 * Typeclass supporting flattening a double nested type within a nested type constructor.
 *
 * @author Owein Reese
 *
 * @tparam M The type over which to flatten
 * @tparam Obj The object over which to lift the flatten.
 */
trait LiftFlatten[M[_], Obj] extends DFunction1[Obj]

@imports[LiftFlatten]
object LiftFlatten

trait LiftFlattenSyntax{

	///Syntax extension providing for a `liftFlatten` method.
	implicit class LiftFlattenOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a flatten operation given the juxtaposition of the two of the given types in the nested type 
		 * structure.
		 *
		 * @tparam M the type over which to flatten given that there exists the concept of flattening of the type.
		 */
		def liftFlatten[M[_]](implicit lift: LiftFlatten[M, F[A]]): lift.Out = lift(fa)
	}
}

//There does not exist Context for liftFlatten.