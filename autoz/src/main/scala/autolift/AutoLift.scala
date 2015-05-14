package autolift

import scalaz.Functor

//TODO: for synthetic transformers flatMap, if can make M[F[A]], A => M[F[B]], and bind.bind(mfa){ something yield M[F[B]] }
//IDEA:
// M[F[A]], A => M[F[B]]
// turn A => M[F[B]] into M[A => F[B]] 
//   using _.flatMap(f) = M[A] => M[F[B]]
//   using applicative ??
// now have M[F[A]], M[A => F[B]] <- that's almost an applicative
// turn M[A => F[B]] into M[F[A] => F[B]]
//  using _.flatMap(f) on A => F[B]
//  M[A => F[B]] map (x => _.flatMap(x))
// now have M[F[A]], M[F[A] => F[B]] <- that's an applicative

//NOW GENERALIZE THE ABOVE TO ARBITRARY MAPPINGS!!

// for M[F[A]], M[F[A => B]]
// turn F[A => B] -> F[A] => F[B]
// now is an applicative!

object All extends AutoLiftImplicits with AutoMapImplicits with Lifters

//Rename! Would like AutoLift._ just like Scalaz._
object AutoLift extends AutoLiftImplicits with Lifters

trait AutoLiftImplicits{
	/** Implicit explosing methods on any type constructor with a valid Functor which provides automatic function 
	 *  lifting based upon the type of the function. 
	 */
	implicit class LiftOps[F[_]: Functor, A](fa: F[A]){
		def liftMap[Function](f: Function)(implicit lift: LiftF[F[A], Function]): lift.Out = lift(fa, f)

		def liftAp[Function](f: Function)(implicit lift: LiftAp[F[A], Function]): lift.Out = lift(fa, f)

		def liftFlatMap[Function](f: Function)(implicit lift: LiftB[F[A], Function]): lift.Out = lift(fa, f)
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