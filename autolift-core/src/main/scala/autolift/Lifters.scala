package autolift

import scalaz.{Functor, Apply, Applicative, Bind, Foldable, Monoid, MonadPlus}
import export._

trait LiftImplicits{
	/** Implicit exposing methods on any type constructor which provides automatic function lifting based upon the 
	 *  type of the function.
	 *
	 * @param fa An instance of `F[A]`.
	 * @tparam F A type constructor
	 * @tparam A The type within `F`.
	 */
	implicit class LifterOps[F[_], A](fa: F[A]){

		def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z)(implicit lift: LiftFoldRight[F[A], (B, => Z) => Z, Z]): lift.Out = 
			lift(fa, f, z)
	}
}

//TODO: split into another file, a la ops, syntax.
trait LiftFunctions{ //These are autolifting contexts

	def liftAp[A, B, F[_]](f: F[A => B])(implicit ap: Apply[F]) = new LiftedAp(f)

	sealed class LiftedAp[A, B, F[_]](protected val f: F[A => B])(implicit ap: Apply[F]){
		def andThen[C >: B, D](lf: LiftedAp[C, D, F]) = new LiftedAp(ap.ap(f)(
			ap.map(lf.f){ 
				y: (C => D) => { x: (A => B) => x andThen y } 
			}
		))

		def compose[C, D <: A](lf: LiftedAp[C, D, F]) = lf andThen this

		def map[C](g: B => C): LiftedAp[A, C, F] = new LiftedAp(ap.map(f){ _ andThen g })

		def apply[That](that: That)(implicit lift: LiftAp[That, F[A => B]]): lift.Out = lift(that, f)
	}

	def liftFlatMap[A, B, M[_]](f: A => M[B])(implicit bind: Bind[M]) = new LiftedFlatMap(f)

	sealed class LiftedFlatMap[A, B, M[_]](protected val f: A => M[B])(implicit bind: Bind[M]){
		def andThen[C >: B, D](that: LiftedFlatMap[C, D, M]) = new LiftedFlatMap({ x: A => bind.bind(f(x))(that.f) })

		def compose[C, D <: A](that: LiftedFlatMap[C, D, M]) = that andThen this

		def map[C](g: B => C): LiftedFlatMap[A, C, M] = new LiftedFlatMap({ x: A => bind.map(f(x))(g) })

		def apply[That](that: That)(implicit lift: LiftB[That, A => M[B]]): lift.Out = lift(that, f)
	}

	def liftFoldMap[A, B](f: A => B)(implicit m: Monoid[B]) = new LiftedFoldMap(f)

	sealed class LiftedFoldMap[A, B](f: A => B)(implicit m: Monoid[B]){
		def andThen[C >: B, D : Monoid](that: LiftedFoldMap[C, D]) = that compose this

		def compose[C, D <: A](that: LiftedFoldMap[C, D]) = that map f

		def map[C : Monoid](g: B => C): LiftedFoldMap[A, C] = new LiftedFoldMap(f andThen g)

		def apply[That](that: That)(implicit lift: LiftFoldMap[That, A => B]): lift.Out = lift(that, f)
	}
}