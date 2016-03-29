package autolift.scalaz

import scalaz.{Functor, Foldable, Unapply}
import autolift.LiftFoldRight


trait ScalazLiftFoldRight[FA, Fn, Z] extends LiftFoldRight[FA, Fn, Z]

object ScalazLiftFoldRight extends LowPriorityScalazLiftFoldRight{
	def apply[FA, Fn, Z](implicit lift: ScalazLiftFoldRight[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (C, => B) => B, B, B] =
		new ScalazLiftFoldRight[F[A], (C, => B) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (C, => B) => B, z: B) = fold.foldRight(fa, z)(f)
		}
}

trait LowPriorityScalazLiftFoldRight extends LowPriorityScalazLiftFoldRight1{
	implicit def unbase[FA, A, C >: A, B](implicit un: Un.Apply[Foldable, FA, A]): Aux[FA, (C, => B) => B, B, B] =
		new ScalazLiftFoldRight[FA, (C, => B) => B, B]{
			type Out = B

			def apply(fa: FA, f: (C, => B) => B, z: B) = un.TC.foldRight(un(fa), z)(f)
		}
}

trait LowPriorityScalazLiftFoldRight1 extends LowPriorityScalazLiftFoldRight2{
	implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldRight[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
		new ScalazLiftFoldRight[F[G], Fn, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

trait LowPriorityScalazLiftFoldRight2{
	type Aux[FA, Fn, Z, Out0] = ScalazLiftFoldRight[FA, Fn, Z]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn, Z](implicit un: Un.Apply[Functor, FG, G], lift: LiftFoldRight[G, Fn, Z]): Aux[FG, Fn, Z, un.M[lift.Out]] =
		new ScalazLiftFoldRight[FG, Fn, Z]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn, z: Z) = un.TC.map(un(fg)){ g: G => lift(g, f, z) }
		}
}

trait LiftFoldRightSyntax extends LowPriorityLiftFoldRightSyntax{
	implicit class LiftFoldRightOps[F[_], A](fa: F[A]){
		def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z)(implicit lift: LiftFoldRight[F[A], (B, => Z) => Z, Z]): lift.Out = 
			lift(fa, f, z)
	}
}

trait LowPriorityLiftFoldRightSyntax{
	implicit class LowLiftFoldRightOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){
		def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z)(implicit lift: LiftFoldRight[FA, (B, => Z) => Z, Z]): lift.Out = 
			lift(fa, f, z)
	}
}

final class LiftedFoldRight[B, Z](z: Z, f: (B, => Z) => Z){
	def apply[That](that: That)(implicit lift: LiftFoldRight[That, (B, => Z) => Z, Z]): lift.Out = lift(that, f, z)
}

trait LiftFoldRightContext{
	def liftFoldRight[B, Z](z: Z)(f: (B, => Z) => Z) = new LiftedFoldRight(z, f)
}