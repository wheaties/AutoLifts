package autolift

/**
 * Typeclass supporting lifting the discovery of the least element over an arbitrary nesting
 * of type constructors, assuming one can be produced.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type which will be lifted and searched
 * @tparam A The type over which to produce a minimum
 */
trait LiftMinimum[Obj, A] extends DFunction1[Obj]

trait LiftMinimumSyntax{
  
  ///Syntax extension providing for a `liftMin` method.
  implicit class LiftMinimumOps[F[_], A](fa: F[A]){

  	/**
  	 * Automatic lifting of a min operation on the first nested type parameter of a Monoid.
  	 *
  	 * @tparam B The type on which to find a minimum, assuming one can be produced
  	 */
    def liftMin[B](implicit lift: LiftMinimum[F[A], B]): lift.Out = lift(fa)
  }
}