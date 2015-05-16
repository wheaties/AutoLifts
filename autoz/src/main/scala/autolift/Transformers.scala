package autolift

import scalaz.{Functor, Apply, Bind}

//TODO:
// 3. FlatMap

trait TransformerF[FA, Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

//TODO: This doesn't force to the bottom! Need Apart, no? Yup!
//TODO: Order of implicits is backwards
object TransformerF extends LowPriorityTransformerF {
	def apply[FA, Function](implicit tf: TransformerF[FA, Function]): Aux[FA, Function, tf.Out] = tf

	implicit def base[F[_], A, C >: A, B](implicit functor: Functor[F]): Aux[F[A], C => B, F[B]] =
		new TransformerF[F[A], C => B]{
			type Out = F[B]

			def apply(fa: F[A], f: C => B) = functor.map(fa)(f)
		}
}

trait LowPriorityTransformerF{
	type Aux[FA, Function, Out0] = TransformerF[FA, Function]{ type Out = Out0 }

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], tf: TransformerF[G, Function]): Aux[F[G], Function, F[tf.Out]] =
		new TransformerF[F[G], Function]{
			type Out = F[tf.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => tf(g, f) }
		}
}

trait TransformerAp[FA, Function] {
	type Out

	def apply(fa: FA, f: Function): Out
}

object TransformerAp extends LowPriorityTransformerAp {
	def apply[FA, Function](implicit tap: TransformerAp[FA, Function]): Aux[FA, Function, tap.Out] = tap

	implicit def base[F[_], A, B](implicit ap: Apply[F]): Aux[F[A], F[A => B], F[B]] =
		new TransformerAp[F[A], F[A => B]]{
			type Out = F[B]

			def apply(fa: F[A], f: F[A => B]) = ap.ap(fa)(f)
		}
}

trait LowPriorityTransformerAp {
	type Aux[FA, Function, Out0] = TransformerAp[FA, Function]{ type Out = Out0 }

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