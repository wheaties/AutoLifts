package autolift

/**
 * Typeclass supporting lifting the discovery of the greatest element over an arbitrary nesting
 * of type constructors, assuming one can be produced.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type which will be lifted and searched
 * @tparam A The type over which to produce a maximum
 */
trait LiftMaximum[Obj, A] extends DFunction1[Obj]

trait LiftMaximumSyntax{
  
  ///Syntax extension providing for a `liftMin` method.
  implicit class LiftMaximumOps[F[_], A](fa: F[A]){

  	/**
  	 * Automatic lifting of a min operation on the first nested type parameter of a Monoid.
  	 *
  	 * @tparam B The type on which to find a maximum, assuming one can be produced
  	 */
    def liftMax[B](implicit lift: LiftMaximum[F[A], B]): lift.Out = lift(fa)
  }
}