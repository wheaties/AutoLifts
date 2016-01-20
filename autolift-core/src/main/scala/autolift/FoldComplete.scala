package autolift


/**
 * Type class supporting folding over a stack of nested type constructors given that the inner most type has an instance of
 * Monoid.
 *
 * @author Owein Reese
 *
 * @tparam Obj The types to be folded.
 */
trait FoldComplete[Obj] extends DFunction1[Obj]

object FoldComplete

trait FoldCompleteSyntax{
	implicit class FoldCompleteOps[F[_], A](fa: F[A]){
		def foldComplete(implicit fold: FoldComplete[F[A]]): fold.Out = fold(fa)
	}
}

//Contexts for foldComplete do not exist

