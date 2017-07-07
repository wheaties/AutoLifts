package autolift.scalaz

import autolift.{LiftTraverse, LiftTraverseSyntax}
import scalaz.{Functor, Applicative, Traverse}

trait ScalazLiftTraverse[Obj, Fn] extends LiftTraverse[Obj, Fn]

object ScalazLiftTraverse extends LowPriorityScalazLiftTraverse{
  def apply[Obj, Fn](implicit lift: ScalazLiftTraverse[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[M[_], A, B, C >: A, F[_]](implicit ap: Applicative[M], traverse: Traverse[F]): Aux[F[A], C => M[B], M[F[B]]] =
    new ScalazLiftTraverse[F[A], C => M[B]]{
      type Out = M[F[B]]

      def apply(fa: F[A], f: C => M[B]) = traverse.traverse(fa)(f)
    }
}

trait LowPriorityScalazLiftTraverse{
  type Aux[Obj, Fn, Out0] = ScalazLiftTraverse[Obj, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftTraverse[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new ScalazLiftTraverse[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], fn: Fn) = functor.map(fg){ g: G => lift(g, fn) }
    }
}

final class LiftedTraverse[M[_], A, B](protected val f: A => M[B])(implicit ap: Applicative[M]){
  def map[C](g: B => C) = new LiftedTraverse({ x: A => ap.map(f(x))(g) })

  def apply[That](that: That)(implicit lift: LiftTraverse[That, A => M[B]]): lift.Out = lift(that, f)
}

trait LiftedTraverseImplicits{
  implicit def functor[M[_], A] = new Functor[LiftedTraverse[M, A, ?]]{
    def map[B, C](lt: LiftedTraverse[M, A, B])(f: B => C) = lt map f
  }
}

trait LiftTraverseContext{
  def liftTraverse[M[_], A, B](f: A => M[B])(implicit ap: Applicative[M]) = new LiftedTraverse(f)
}

trait LiftTraverseExport{
  implicit def mkTv[Obj, Fn](implicit lift: ScalazLiftTraverse[Obj, Fn]): ScalazLiftTraverse.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftTraversePackage extends LiftTraverseExport
  with LiftTraverseContext
  with LiftTraverseSyntax