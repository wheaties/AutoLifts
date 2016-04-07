package autolift.scalaz

import scalaz.{Functor, Foldable, Unapply}
import autolift.{LiftFoldLeft, LiftFoldLeftSyntax}


trait ScalazLiftFoldLeft[Obj, Fn, Z] extends LiftFoldLeft[Obj, Fn, Z]

object ScalazLiftFoldLeft extends LowPriorityScalazLiftFoldLeft{
	def apply[FA, Fn, Z](implicit lift: ScalazLiftFoldLeft[FA, Fn, Z]): Aux[FA, Fn, Z, lift.Out] = lift

	implicit def base[F[_], A, C >: A, B](implicit fold: Foldable[F]): Aux[F[A], (B, C) => B, B, B] =
		new ScalazLiftFoldLeft[F[A], (B, C) => B, B]{
			type Out = B

			def apply(fa: F[A], f: (B, C) => B, z: B) = fold.foldLeft(fa, z)(f)
		}
}

trait LowPriorityScalazLiftFoldLeft extends LowPriorityScalazLiftFoldLeft1{
	implicit def unbase[FA, A, C >: A, B](implicit un: Un.Apply[Foldable, FA, A]): Aux[FA, (B, C) => B, B, B] =
		new ScalazLiftFoldLeft[FA, (B, C) => B, B]{
			type Out = B

			def apply(fa: FA, f: (B, C) => B, z: B) = un.TC.foldLeft(un(fa), z)(f)
		}
}

trait LowPriorityScalazLiftFoldLeft1 extends LowPriorityScalazLiftFoldLeft2{
	implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
		new ScalazLiftFoldLeft[F[G], Fn, Z]{
			type Out = F[lift.Out]

			def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
		}
}

trait LowPriorityScalazLiftFoldLeft2{
	type Aux[FA, Fn, Z, Out0] = ScalazLiftFoldLeft[FA, Fn, Z]{ type Out = Out0 }

	implicit def unrecur[FG, G, Fn, Z](implicit un: Un.Apply[Functor, FG, G], lift: LiftFoldLeft[G, Fn, Z]): Aux[FG, Fn, Z, un.M[lift.Out]] =
		new ScalazLiftFoldLeft[FG, Fn, Z]{
			type Out = un.M[lift.Out]

			def apply(fg: FG, f: Fn, z: Z) = un.TC.map(un(fg)){ g: G => lift(g, f, z) }
		}
}

trait ScalazLiftFoldLeftSyntax extends LiftFoldLeftSyntax with LowPriorityLiftFoldLeftSyntax

trait LowPriorityLiftFoldLeftSyntax{

  /// Syntax extension provided for a `liftFoldLeft` method.
  implicit class LowLiftFoldLeftOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a FoldL dicated by the argument type of the function.
     *
     * @param z the initial value which starts the fold.
     * @param f the function which defines the fold.
     * @tparam B the type to be folded.
     * @tparam Z the resultant type of the fold.
     */
    def liftFoldLeft[B, Z](z: Z)(f: (Z, B) => Z)(implicit lift: LiftFoldLeft[FA, (Z, B) => Z, Z]): lift.Out = 
      lift(fa, f, z)
  }
}