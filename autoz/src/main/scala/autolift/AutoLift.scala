package autolift

import scalaz.Functor

object All extends AutoLiftImplicits with AutoMapImplicits with Lifters

object AutoLift extends AutoLiftImplicits

trait AutoLiftImplicits{
	/** Implicit explosing methods on any type constructor with a valid Functor which provides automatic function 
	 *  lifting based upon the type of the function. 
	 */
	implicit class LiftOps[F[_]: Functor, A](fa: F[A]){
		def liftMap[Function](f: Function)(implicit mapper: Mapper[F[A], Function]): mapper.Out = mapper(fa, f)

		def liftAp[Function](f: Function)(implicit ap: Ap[F[A], Function]): ap.Out = ap(fa, f)

		def liftFlatMap[Function](f: Function)(implicit fm: FlatMapper[F[A], Function]): fm.Out = fm(fa, f)
	}
}

object AutoMap extends AutoMapImplicits

trait AutoMapImplicits{
	/** Implicit exposing methods on any type constructor with a valid Functor which provides automatic type driven method
	 *  invocation of either a Bind or fmap, assuming that a Bind exists for that type. 
	 */
	implicit class AutoOps[F[_]: Functor, A](fa: F[A]){
		def autoMap[Function](f: Function)(implicit dm: DepMap[F[A], Function]): dm.Out = dm(fa, f)
	}
}