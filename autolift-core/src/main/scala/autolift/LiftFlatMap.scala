package autolift

import export._

/**
 * Type class supporting flat mapping a function over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Funciton The function to be lifted.
 */
trait LiftFlatMap[Obj, Function] extends DFunction2[Obj, Function]

@imports[LiftFlatMap]
object LiftFlatMap

trait LiftFlatMapSyntax{

	/// Syntax extension providing for a `liftFlatMap` method.
	implicit class LiftFlatMapOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting and flattening of the contained function `f` such that the application point is dicated by the
		 * argument and return type of the function.
		 *
		 * @param f the function that returns a type with a Monad.
		 * @tparam B the argument type of the function.
		 * @tparam C the inner type of the return type of the function.
		 * @tparam M the higher-kinded type of the return type of the function which has a Monad.
		 */
		def liftFlatMap[B, C, M[_]](f: B => M[C])(implicit lift: LiftFlatMap[F[A], B => M[C]]): lift.Out = lift(fa, f)
	}
}

//See individual implementations for liftFlatMap Context.