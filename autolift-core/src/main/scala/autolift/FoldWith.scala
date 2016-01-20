package autolift


/**
 * Type class supporting folding over a nested stack of type constructors given a mapping from a type to something which
 * has a Monoid.
 *
 * @author Owein Reese
 *
 * @tparam Obj The types to be folded.
 * @tparam Function The function over which to map and then fold.
 */
trait FoldWith[Obj, Function] extends DFunction2[Obj, Function]

object FoldWith {
	type Aux[Obj, Fn, Out0] = FoldWith[Obj, Fn]{ type Out = Out0 }
}

trait FoldWithSyntax{
	implicit class FoldWithOps[F[_], A](fa: F[A]){
		def foldWith[B, C](f: B => C)(implicit lift: FoldWith[F[A], B => C]): lift.Out = lift(fa, f)
	}
}

//See individual instances for implementations of Context.

