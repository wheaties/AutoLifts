package autolift

/**
 * Typeclass supporting lifting the discovery of the least element over an arbitrary nesting
 * of type constructors, assuming one can be produced.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type which will be lifted and searched
 * @tparam Function The function used to produce the values by which a minimum will be chosen
 */
trait LiftMinimumBy[Obj, Function] extends DFunction2[Obj, Function]

trait LiftMinimumBySyntax{
  
  ///Syntax extension providing for a `liftMinBy` method.
  implicit class LiftMinimumByOps[F[_], A](fa: F[A]){

  	/**
  	 * Automatic lifting of a min dictated by the signature of a function and given that the mapping maps one type 
     * to another which has a Monoid.
  	 *
  	 * @tparam B The type on which to find a minimum, assuming one can be produced
  	 */
    def liftMinBy[B, C](f: B => C)(implicit lift: LiftMinimumBy[F[A], B => C]): lift.Out = lift(fa, f)
  }
}

//See individual instances for liftMinimumBy Context.