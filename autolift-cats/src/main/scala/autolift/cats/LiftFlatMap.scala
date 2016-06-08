package autolift.cats

import cats.{FlatMap, Functor, Unapply}
import autolift.{LiftFlatMap, LiftFlatMapSyntax}


trait CatsLiftFlatMap[Obj, Fn] extends LiftFlatMap[Obj, Fn]

object CatsLiftFlatMap extends LowPriorityCatsLiftFlatMap {
  def apply[Obj, Fn](implicit lift: CatsLiftFlatMap[Obj, Fn]): Aux[Obj, Fn, lift.Out] = lift

  implicit def base[M[_], A, C >: A, B](implicit flatMap: FlatMap[M]): Aux[M[A], C => M[B], M[B]] =
    new CatsLiftFlatMap[M[A], C => M[B]]{
      type Out = M[B]

      def apply(ma: M[A], f: C => M[B]) = flatMap.flatMap(ma)(f)
    }
}

trait LowPriorityCatsLiftFlatMap extends LowPriorityCatsLiftFlatMap1{
  implicit def recur[F[_], G, Fn](implicit functor: Functor[F], lift: LiftFlatMap[G, Fn]): Aux[F[G], Fn, F[lift.Out]] =
    new CatsLiftFlatMap[F[G], Fn]{
      type Out = F[lift.Out]

      def apply(fg: F[G], f: Fn) = functor.map(fg){ g: G => lift(g, f) }
    }
}

trait LowPriorityCatsLiftFlatMap1{
  type Aux[Obj, Fn, Out0] = CatsLiftFlatMap[Obj, Fn]{ type Out = Out0 }

  implicit def unrecur[FG, G, Fn](implicit unapply: Un.Apply[Functor, FG, G], lift: LiftFlatMap[G, Fn]): Aux[FG, Fn, unapply.M[lift.Out]] =
    new CatsLiftFlatMap[FG, Fn]{
      type Out = unapply.M[lift.Out]

      def apply(fg: FG, f: Fn) = unapply.TC.map(unapply.subst(fg)){ g: G => lift(g, f) }
    }
}

trait CatsLiftFlatMapSyntax extends LiftFlatMapSyntax with LowLiftFlatMapSyntax

trait LowLiftFlatMapSyntax{

  /// Syntax extension providing for a `liftFlatMap` method.
  implicit class LowLiftFlatMapOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting and flattening of the contained function `f` such that the application point is dicated by the
     * argument and return type of the function.
     *
     * @param f the function that returns a type with a Monad.
     * @tparam B the argument type of the function.
     * @tparam C the inner type of the return type of the function.
     * @tparam M the higher-kinded type of the return type of the function which has a Monad.
     */
    def liftFlatMap[B, C, M[_]](f: B => M[C])(implicit lift: LiftFlatMap[FA, B => M[C]]): lift.Out = lift(fa, f)
  }
}

final class LiftedFlatMap[A, B, M[_]](protected val f: A => M[B])(implicit flatMap: FlatMap[M]){
  def andThen[C >: B, D](that: LiftedFlatMap[C, D, M]) = new LiftedFlatMap({ x: A => flatMap.flatMap(f(x))(that.f) })

  def compose[C, D <: A](that: LiftedFlatMap[C, D, M]) = that andThen this

  def map[C](g: B => C): LiftedFlatMap[A, C, M] = new LiftedFlatMap({ x: A => flatMap.map(f(x))(g) })

  def apply[That](that: That)(implicit lift: LiftFlatMap[That, A => M[B]]): lift.Out = lift(that, f)
}

trait LiftedFlatMapImplicits{
  implicit def liftedFlatMapFunctor[A, M[_]] = new Functor[LiftedFlatMap[A, ?, M]]{
    def map[B, C](lb: LiftedFlatMap[A, B, M])(f: B => C) = lb map f
  }
}

trait LiftFlatMapContext{
  def liftFlatMap[A, B, M[_]](f: A => M[B])(implicit flatMap: FlatMap[M]) = new LiftedFlatMap(f)
}

trait LiftFlatMapExport{
  implicit def mkFM[Obj, Fn](implicit lift: CatsLiftFlatMap[Obj, Fn]): CatsLiftFlatMap.Aux[Obj, Fn, lift.Out] = lift
}

trait LiftFlatMapPackage extends LiftedFlatMapImplicits
  with LiftFlatMapExport
  with LiftFlatMapContext
  with CatsLiftFlatMapSyntax