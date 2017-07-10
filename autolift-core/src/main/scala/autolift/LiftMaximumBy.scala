package autolift

/**
 * Typeclass supporting lifting the discovery of the greatest element over an arbitrary nesting
 * of type constructors, assuming one can be produced.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type which will be lifted and searched
 * @tparam Function The function used to produce the values by which a maximum will be chosen
 */
trait LiftMaximumBy[Obj, Function] extends DFunction2[Obj, Function]

trait LiftMaximumBySyntax{
  
  ///Syntax extension providing for a `liftMinBy` method.
  implicit class LiftMaximumByOps[F[_], A](fa: F[A]){

  	/**
  	 * Automatic lifting of a max dictated by the signature of a function and given that the mapping maps one type 
     * to another which has a Monoid.
  	 *
  	 * @tparam B The type on which to find a maximum, assuming one can be produced
  	 */
    def liftMaxBy[B, C](f: B => C)(implicit lift: LiftMaximumBy[F[A], B => C]): lift.Out = lift(fa, f)
  }
}

//See individual instances for liftMaximumBy Context.