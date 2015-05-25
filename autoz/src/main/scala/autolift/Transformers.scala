package autolift

import scalaz.{Functor, Apply, Bind}
import annotation.implicitNotFound

object Transformers extends Transformers

trait Transformers extends TransformerImplicits

trait TransformerImplicits{
	implicit class TransformerOps[F[_]: Functor, A](fa: F[A]){
		def transformMap[Function](f: Function)(implicit trans: TransformerF[F[A], Function]): trans.Out = trans(fa, f)

		def transformAp[Function](f: Function)(implicit trans: TransformerAp[F[A], Function]): trans.Out = trans(fa, f)
	}
}

/**
 * Type class witnessing that `FA` is a higher-kinded type.
 *
 * @author Owein Reese
 *
 * @tparam FA The type to witness.
 */
trait Apart[FA]{
	type W[X]
	type T

	def apply(fa: FA): W[T]
}

object Apart{
	def apply[FA](implicit apart: Apart[FA]): Aux[FA, apart.W, apart.T] = apart

	type Aux[FA, F[_], A0] = Apart[FA]{ type W[X] = F[X]; type T = A0 }

	implicit def mk[F[_], A0]: Aux[F[A0], F, A0] =
		new Apart[F[A0]]{
			type W[X] = F[X]
			type T = A0

			def apply(fa: F[A0]) = fa
		}
}

/**
 * Type class supporting a nested stack of type constructors can behave as an instance of a single Functor and thus
 * map a function over them.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type stacks
 * @tparam Function The function to be mapped.
 */
 @implicitNotFound("Can not prove that ${Function} operates on the inner most type of ${Obj} which defined a Functor.")
trait TransformerF[Obj, Function] extends DFunction2[Obj, Function]

object TransformerF extends LowPriorityTransformerF {
	def apply[FA, Function](implicit tf: TransformerF[FA, Function]): Aux[FA, Function, tf.Out] = tf

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], tf: TransformerF[G, Function]): Aux[F[G], Function, F[tf.Out]] =
		new TransformerF[F[G], Function]{
			type Out = F[tf.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => tf(g, f) }
		}
}

trait LowPriorityTransformerF{
	type Aux[FA, Function, Out0] = TransformerF[FA, Function]{ type Out = Out0 }

	implicit def err[F[_], GA, G[_], A, Function](implicit ev1: Functor[F], ev2: Functor[G], ap: Apart.Aux[GA, G, A]): Aux[F[GA], Function, Nothing]
		= sys.error("implicit divergence")

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new TransformerF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

/**
 * Type class supporting a nested stack of type constructors can behave as an instance of a single Applicative and thus
 * apply a function over them.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type stacks
 * @tparam Function The function to be applicatively applied.
 */
@implicitNotFound("Can not prove that ${Function} operates on the inner most type of ${Obj} which defined an Applicative.")
trait TransformerAp[FA, Function] extends DFunction2[FA, Function]

object TransformerAp extends LowPriorityTransformerAp {
	def apply[FA, Function](implicit tap: TransformerAp[FA, Function]): Aux[FA, Function, tap.Out] = tap

	implicit def recur[F[_], G, Function](implicit ap: Apply[F], tap: TransformerAp[G, Function]): Aux[F[G], F[Function], F[tap.Out]] =
		new TransformerAp[F[G], F[Function]]{
			type Out = F[tap.Out]

			def apply(fg: F[G], f: F[Function]) = ap.ap(fg){ 
				ap.map(f){ inner: Function => 
					{ g:G => tap(g, inner) } //F1[F2...Fn[A => B]] into F1[F2...[Fn[A]]] => F1[F2[...Fn[B]]]
				}
			}
		}
}

trait LowPriorityTransformerAp {
	type Aux[FA, Function, Out0] = TransformerAp[FA, Function]{ type Out = Out0 }

	implicit def err[F[_], GA, G[_], A, Function](implicit ev1: Apply[F], ev2: Apply[G], ap: Apart.Aux[GA, G, A]): Aux[F[GA], Function, Nothing]
		= sys.error("implicit divergence")

	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new TransformerAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}