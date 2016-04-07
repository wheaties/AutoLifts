package autolift.scalaz

import scalaz.{Functor, Bind, Unapply}
import autolift.{LiftFlatten, LiftFlattenSyntax}

trait ScalazLiftFlatten[M[_], Obj] extends LiftFlatten[M, Obj]

object ScalazLiftFlatten extends LowPriorityScalazLiftFlatten{
	def apply[M[_], Obj](implicit lift: ScalazLiftFlatten[M, Obj]): Aux[M, Obj, lift.Out] = lift

	implicit def base[M[_], A](implicit bind: Bind[M]): Aux[M, M[M[A]], M[A]] =
		new ScalazLiftFlatten[M, M[M[A]]]{
			type Out = M[A]

			def apply(mma: M[M[A]]) = bind.bind(mma){ ma: M[A] => ma }
		}
}

trait LowPriorityScalazLiftFlatten extends LowPriorityScalazLiftFlatten1{
	implicit def recur[M[_], F[_], G](implicit functor: Functor[F], lift: LiftFlatten[M, G]): Aux[M, F[G], F[lift.Out]] =
		new ScalazLiftFlatten[M, F[G]]{
			type Out = F[lift.Out]

			def apply(fg: F[G]) = functor.map(fg){ g: G => lift(g) }
		}
}

trait LowPriorityScalazLiftFlatten1{
	type Aux[M[_], Obj, Out0] = ScalazLiftFlatten[M, Obj]{ type Out = Out0 }

	implicit def unrecur[M[_], FG, G](implicit un: Un.Apply[Functor, FG, G], lift: LiftFlatten[M, G]): Aux[M, FG, un.M[lift.Out]] =
		new ScalazLiftFlatten[M, FG]{
			type Out = un.M[lift.Out]

			def apply(fg: FG) = un.TC.map(un(fg)){ g: G => lift(g) }
		}
}

trait ScalazLiftFlattenSyntax extends LiftFlattenSyntax with LowPriorityLiftFlattenSyntax

trait LowPriorityLiftFlattenSyntax{

  ///Syntax extension providing for a `liftFlatten` method.
  implicit class LowLiftFlattenOps[FA](fa: FA)(implicit ev: Unapply[Functor, FA]){

    /**
     * Automatic lifting of a flatten operation given the juxtaposition of the two of the given types in the nested type 
     * structure.
     *
     * @tparam M the type over which to flatten given that there exists the concept of flattening of the type.
     */
    def liftFlatten[M[_]](implicit lift: LiftFlatten[M, FA]): lift.Out = lift(fa)
  }
}