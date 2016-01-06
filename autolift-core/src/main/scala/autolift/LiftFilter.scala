package autolift

import export._

/**
 * Typeclass supporting filtering a set of values within a nested type constructor.
 * 
 * @author Owein Reese
 * 
 * @tparam Obj The object over which to lifter the filter
 * @tparam Function The predicate which determines if a value is included in the final result
 */
trait LiftFilter[Obj, Function] extends ((Obj, Function) => Obj)

@imports[LiftFilter]
object LiftFilter

trait LiftFilterSyntax{

	/// Syntax extension providing for a `liftFilter` method.
	implicit class LiftFilterOps[F[_], A](fa: F[A]){

		/**
		 * Automatic lifting of a predicate dictated by the function type signature.
		 *
		 * @param f the predicate function used to filter.
		 * @tparam the argument type of the predicate
		 */
		def liftFilter[B](f: B => Boolean)(implicit lift: LiftFilter[F[A], B => Boolean]): F[A] = lift(fa, f)
	}
}

final class LiftedFilter[A](f: A => Boolean){
	def apply[That](that: That)(implicit lift: LiftFilter[That, A => Boolean]): That = lift(that, f)
}

trait LiftFilterContext{
	def liftFilter[A](f: A => Boolean) = new LiftedFilter(f)
}