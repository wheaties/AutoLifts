package autolift.scalaz

import scalaz.{Zip, Functor}
import autolift.{LiftZipWith, LiftedZipWith}

trait ScalazLiftZipWith[Obj1, Obj2, Fn] extends LiftZipWith[Obj1, Obj2, Fn]

object ScalazLiftZipWith extends LowerPriorityScalazLiftZipWith{
  def apply[Obj1, Obj2, Fn](implicit lift: ScalazLiftZipWith[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

  implicit def zipped[F[_], G, H, G1 >: G, H1 >: H, Out0](implicit zip: Zip[F], functor: Functor[F]): Aux[F[G], F[H], (G1, H1) => Out0, F[Out0]] =
    new ScalazLiftZipWith[F[G], F[H], (G1, H1) => Out0]{
      type Out = F[Out0]

      def apply(fg: F[G], fh: F[H], f: (G1, H1) => Out0) = zip.zipWith(fg, fh)(f)
    }
}

trait LowerPriorityScalazLiftZipWith {
  type Aux[Obj1, Obj2, Fn, Out0] = ScalazLiftZipWith[Obj1, Obj2, Fn]{ type Out = Out0 }

  implicit def recur[F[_], G, H, Fn](implicit functor: Functor[F], lift: LiftZipWith[G, H, Fn]): Aux[F[G], H, Fn, F[lift.Out]] =
    new ScalazLiftZipWith[F[G], H, Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H, f: Fn) = functor.map(fg){ g: G => lift(g, h, f) }
    }
}

trait LiftedZipWithImplicits{
  implicit def liftedZipWithFunctor[A, B] = new Functor[LiftedZipWith[A, B, ?]]{
    def map[C, D](lm: LiftedZipWith[A, B, C])(f: C => D) = lm map f
  }
}