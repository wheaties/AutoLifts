package autolift


/**
 * Type class supporting folding on a stack of type constructors up to and included a type `F` but nothing more.
 *
 * @author Owein Reese
 *
 * @tparam F The type constructor up to which folding should occur.
 * @tparam Obj the types to fold over.
 */
trait FoldOver[F[_], Obj] extends DFunction1[Obj]

object FoldOver{
	type Aux[F[_], Obj, Out0] = FoldOver[F, Obj]{ type Out = Out0 }
}

trait FoldOverSyntax{
	implicit class FoldOverOps[F[_], A](fa: F[A]){
		def foldOver[M[_]](implicit fold: FoldOver[M, F[A]]): fold.Out = fold(fa)
	}
}

//Contexts for foldOver do not exist.

