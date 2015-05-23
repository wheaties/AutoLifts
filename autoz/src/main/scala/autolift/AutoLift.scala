package autolift

import scalaz.{Functor, Foldable}

object All extends AutoLiftImplicits with Lifters with AutoMapImplicits with Mappers with AutoTransformImplicits

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

		def liftFoldLeft[Function, Z](z: Z)(f: Function)(implicit lift: LiftFoldLeft[F[A], Function, Z]): lift.Out = 
			lift(fa, f, z)

		def liftFoldRight[Function, Z](z: Z)(f: Function)(implicit lift: LiftFoldRight[F[A], Function, Z]): lift.Out = 
			lift(fa, f, z)

		def liftFold(implicit lift: LiftFold[F[A]]): lift.Out = lift(fa)

		def liftFoldMap[Function](f: Function)(implicit lift: LiftFoldMap[F[A], Function]): lift.Out = lift(fa, f)

		//TODO: Move trait to Lifters.scala and rename
		def liftFoldAt[M[_]](implicit fold: FoldedUpTo[M, F[A]]): fold.Out = fold(fa)
	}

	//TODO: This should be in a FolderImplicits
	implicit class FolderOps[F[_]: Foldable, A](fa: F[A]){
		def foldWith[Function](f: Function)(implicit lift: FoldMap[F[A], Function]): lift.Out = lift(fa, f)

		def foldAll(implicit fold: FoldAll[F[A]]): fold.Out = fold(fa)

		//TODO: Fix naming of FoldedOver to FoldOver
		def foldOver[M[_]](implicit fold: FoldedOver[M, F[A]]): fold.Out = fold(fa)
	}
}

object AutoMap extends AutoMapImplicits with Mappers

trait AutoMapImplicits{
	/** Implicit exposing methods on any type constructor with a valid Functor which provides automatic type driven method
	 *  invocation of either a Bind or fmap, assuming that a Bind exists for that type. 
	 */
	implicit class MapperOps[F[_]: Functor, A](fa: F[A]){
		def autoMap[Function](f: Function)(implicit dm: DepMap[F[A], Function]): dm.Out = dm(fa, f)
	}
}

object AutoTransform extends AutoTransformImplicits

//TODO: Folds for these!
trait AutoTransformImplicits{
	implicit class TransformerOps[F[_]: Functor, A](fa: F[A]){
		def transformMap[Function](f: Function)(implicit trans: TransformerF[F[A], Function]): trans.Out = trans(fa, f)

		def transformAp[Function](f: Function)(implicit trans: TransformerAp[F[A], Function]): trans.Out = trans(fa, f)
	}
}