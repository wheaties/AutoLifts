package autolift

import scalaz.{Functor, Foldable}

//TODO: Delete this.
object All extends AutoLiftImplicits with Lifters with AutoTransformImplicits

//Rename! Would like AutoLift._ just like Scalaz._
object AutoLift extends Lifters

trait AutoLiftImplicits{
	//TODO: This should be in a FolderImplicits
	implicit class FolderOps[F[_]: Foldable, A](fa: F[A]){
		def foldWith[Function](f: Function)(implicit lift: FoldMap[F[A], Function]): lift.Out = lift(fa, f)

		def foldAll(implicit fold: FoldAll[F[A]]): fold.Out = fold(fa)

		//TODO: Fix naming of FoldedOver to FoldOver
		def foldOver[M[_]](implicit fold: FoldedOver[M, F[A]]): fold.Out = fold(fa)
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