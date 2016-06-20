package autolift

/**
 * Type class supporting the folding of a predicate over an arbitrary nesting of type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj the type to be lifted into.
 * @tparam Fn the predicate to be lifted.
 */
trait LiftExists[Obj, Fn] extends DFunction2[Obj, Fn]

trait LiftExistsSyntax{
	implicit class LiftExistsOps[F[_], A](fa: F[A]){
		def liftExists[B](f: B => Boolean)(implicit lift: LiftExists[F[A], B => Boolean]): lift.Out = lift(fa, f)
	}
}

final class LiftedExists[A](f: A => Boolean){
	def apply[That](that: That)(implicit lift: LiftExists[That, A => Boolean]): lift.Out = lift(that, f)
}

trait LiftExistsContext{
	def liftExists[A](f: A => Boolean) = new LiftedExists(f)
}