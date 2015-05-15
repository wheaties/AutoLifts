package autolift

import scalaz.Functor


object All extends AutoLiftImplicits with AutoMapImplicits with Lifters with AutoTransformImplicits

//Rename! Would like AutoLift._ just like Scalaz._
object AutoLift extends AutoLiftImplicits with Lifters

trait AutoLiftImplicits{
	/** Implicit explosing methods on any type constructor with a valid Functor which provides automatic function 
	 *  lifting based upon the type of the function. 
	 */
	implicit class LifterOps[F[_]: Functor, A](fa: F[A]){
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
	implicit class MapperOps[F[_]: Functor, A](fa: F[A]){
		def autoMap[Function](f: Function)(implicit dm: DepMap[F[A], Function]): dm.Out = dm(fa, f)
	}
}

object AutoTransform extends AutoTransformImplicits

trait AutoTransformImplicits{
	implicit class TransformerOps[F[_]: Functor, A](fa: F[A]){
		def transformMap[Function](f: Function)(implicit trans: TransformerF[F[A], Function]): trans.Out = trans(fa, f)

		def transformAp[Function](f: Function)(implicit trans: TransformerAp[F[A], Function]): trans.Out = trans(fa, f)
	}
}