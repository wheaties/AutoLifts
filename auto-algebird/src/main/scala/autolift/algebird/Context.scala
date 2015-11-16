package autolift.algebird

import autolift._
import com.twitter.algebird.{Applicative, Monad}

//TODO: ScalaDocs

trait Contexts extends LiftMapContext with LiftMContexts with LiftAContexts{
	def liftAp[A, B, F[_]](f: F[A => B])(implicit ap: Applicative[F]) = new LiftedAp(f)

	def liftFlatMap[A, B, M[_]](f: A => M[B])(implicit fm: Monad[M]) = new LiftedFlatMap(f)

	def liftJoinWith[A, B, C](f: (A, B) => C) = new LiftedJoinWith(f)
}

final class LiftedAp[A, B, F[_]](protected val f: F[A => B])(implicit ap: Applicative[F]){
	def andThen[C >: B, D](lf: LiftedAp[C, D, F]) = new LiftedAp(ap.joinWith(f, lf.f){
		(f1, f2) => f1 andThen f2
	})

	def compose[C, D <: A](lf: LiftedAp[C, D, F]) = lf andThen this

	def map[C](g: B => C): LiftedAp[A, C, F] = new LiftedAp(ap.map(f){ _ andThen g })

	def apply[That](that: That)(implicit lift: LiftAp[That, F[A => B]]): lift.Out = lift(that, f)
}

final class LiftedFlatMap[A, B, M[_]](protected val f: A => M[B])(implicit fm: Monad[M]){
	def andThen[C >: B, D](that: LiftedFlatMap[C, D, M]) = new LiftedFlatMap({ x: A => fm.flatMap(f(x))(that.f) })

	def compose[C, D <: A](that: LiftedFlatMap[C, D, M]) = that andThen this

	def map[C](g: B => C): LiftedFlatMap[A, C, M] = new LiftedFlatMap({ x: A => fm.map(f(x))(g) })

	def apply[That](that: That)(implicit lift: LiftB[That, A => M[B]]): lift.Out = lift(that, f)
}

//TODO: This is LiftedA2!
final class LiftedJoinWith[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedJoinWith[A, B, D] = new LiftedJoinWith({ (a: A, b: B) => g(f(a, b)) })

	def apply[Obj1, Obj2](obj1: Obj1, obj2: Obj2)(implicit lift: LiftJoinWith[Obj1, Obj2, (A, B) => C]): lift.Out = 
		lift(obj1, obj2, f)
}