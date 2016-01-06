package autolift

import export._

/**
 * Type class supporting the mapping over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj the type to be lifted into.
 * @tparam Function the function to be lifted.
 */
trait LiftMap[Obj, Function] extends DFunction2[Obj, Function]

@imports[LiftMap]
object LiftMap

trait LiftMapSyntax{

	/// Syntax extension providing for a `liftMap` method.
	implicit class LiftMapOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of the function `f` over the object such that the application point is dictated by the type
		 * of function invocation.
		 *
		 * @param f the function to be lifted.
		 * @tparam B the argument type of the function.
		 * @tparam C the return type of the function.
		 */
		def liftMap[B, C](f: B => C)(implicit lift: LiftMap[F[A], B => C]): lift.Out = lift(fa, f)
	}
}

final class LiftedMap[A, B](f: A => B){
	def andThen[C >: B, D](that: LiftedMap[C, D]) = that compose this

	def compose[C, D <: A](that: LiftedMap[C, D]) = that map f

	def map[C](g: B => C): LiftedMap[A, C] = new LiftedMap(f andThen g)

	def apply[That](that: That)(implicit lift: LiftMap[That, A => B]): lift.Out = lift(that, f)
}

trait LiftMapContext{
	def liftMap[A, B](f: A => B) = new LiftedMap(f)
}