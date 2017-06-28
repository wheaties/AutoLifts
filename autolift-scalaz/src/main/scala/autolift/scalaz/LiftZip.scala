package autolift.scalaz

import scalaz.{Zip, Functor}
import autolift.LiftZip

trait ScalazLiftZip[Obj1, Obj2] extends LiftZip[Obj1, Obj2]

object ScalazLiftZip extends LowerPriorityScalazLiftZip{
  def apply[Obj1, Obj2](implicit lift: ScalazLiftZip[Obj1, Obj2]): Aux[Obj1, Obj2, lift.Out] = lift

  implicit def base[F[_], G, H](implicit zip: Zip[F]): Aux[F[G], F[H], F[(G, H)]] =
    new ScalazLiftZip[F[G], F[H]]{
      type Out = F[(G, H)]

      def apply(fg: F[G], fh: F[H]) = zip.zip(fg, fh)
    }
}

trait LowerPriorityScalazLiftZip {
  type Aux[Obj1, Obj2, Out0] = ScalazLiftZip[Obj1, Obj2]{ type Out = Out0 }

  implicit def recur[F[_], G, H](implicit functor: Functor[F], lift: LiftZip[G, H]): Aux[F[G], H, F[lift.Out]] =
    new ScalazLiftZip[F[G], H]{
      type Out = F[lift.Out]

      def apply(fg: F[G], h: H) = functor.map(fg){ g: G => lift(g, h) }
    }
}