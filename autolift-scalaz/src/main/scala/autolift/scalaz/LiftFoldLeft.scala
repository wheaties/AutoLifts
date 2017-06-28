package autolift.scalaz

import scalaz.{Functor, Foldable}
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

trait LowPriorityScalazLiftFoldLeft{
  type Aux[FA, Fn, Z, Out0] = ScalazLiftFoldLeft[FA, Fn, Z]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn, Z](implicit functor: Functor[F], lift: LiftFoldLeft[G, Fn, Z]): Aux[F[G], Fn, Z, F[lift.Out]] =
    new ScalazLiftFoldLeft[F[G], Fn, Z]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn, z: Z) = functor.map(fg){ g: G => lift(g, f, z) }
    }
}

trait ScalazLiftFoldLeftSyntax extends LiftFoldLeftSyntax with LowPriorityLiftFoldLeftSyntax

trait LowPriorityLiftFoldLeftSyntax{

  /// Syntax extension provided for a `liftFoldLeft` method.
  implicit class LowLiftFoldLeftOps[F[_], A](fa: F[A])(implicit ev: Functor[F]){

    /**
     * Automatic lifting of a FoldL dicated by the argument type of the function.
     *
     * @param z the initial value which starts the fold.
     * @param f the function which defines the fold.
     * @tparam B the type to be folded.
     * @tparam Z the resultant type of the fold.
     */
    def liftFoldLeft[B, Z](z: Z)(f: (Z, B) => Z)(implicit lift: LiftFoldLeft[F[A], (Z, B) => Z, Z]): lift.Out = 
      lift(fa, f, z)
  }
}