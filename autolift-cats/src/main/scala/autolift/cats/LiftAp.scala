package autolift.cats

import cats.{Functor, Apply}
import autolift.{LiftAp, LiftApSyntax}

trait CatsLiftAp[Obj, Fn] extends LiftAp[Obj, Fn] with Serializable

object CatsLiftAp extends LowPriorityCatsLiftAp {
  def apply[Obj, Fn](implicit lift: CatsLiftAp[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
    new CatsLiftAp[F[A], F[A => B]]{
      type Out = F[B]

      def apply(fa: F[A], f: F[A => B]) = ap.ap(f)(fa)
    }
}

trait LowPriorityCatsLiftAp{
  type Aux[Obj, Fn, Out0] = CatsLiftAp[Obj, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftAp[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftAp[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

final class LiftedAp[A, B, F[_]](protected val f: F[A => B])(implicit ap: Apply[F]){
  def andThen[C >: B, D](lf: LiftedAp[C, D, F]) = new LiftedAp(ap.ap(
    ap.map(lf.f){
      y: (C => D) => { x: (A => B) => x andThen y }
    }
  )(f))

  def compose[C, D <: A](lf: LiftedAp[C, D, F]) = lf andThen this

  def map[C](g: B => C): LiftedAp[A, C, F] = new LiftedAp(ap.map(f){ _ andThen g })

  def apply[That](that: That)(implicit lift: LiftAp[That, F[A => B]]): lift.Out = lift(that, f)
}

trait LiftApPackage extends LiftApSyntax{
  implicit def liftedApFunctor[A, F[_]] = new Functor[LiftedAp[A, ?, F]]{
    def map[B, C](lap: LiftedAp[A, B, F])(f: B => C) = lap map f
  }

  implicit def mkAp[Obj, Fn](implicit lift: CatsLiftAp[Obj, Fn]): CatsLiftAp.Aux[Obj, Fn, lift.Out] = lift

  def liftAp[A, B, F[_]](f: F[A => B])(implicit ap: Apply[F]) = new LiftedAp(f)
}