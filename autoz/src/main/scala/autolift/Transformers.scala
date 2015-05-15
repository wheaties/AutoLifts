package autolift

import scalaz.{Functor, Apply, Bind}

//TODO:
// 1. Map
// 2. Applicative
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

//TODO: for synthetic transformers flatMap, if can make M[F[A]], A => M[F[B]], and bind.bind(mfa){ something yield M[F[B]] }
//IDEA:
// M[F[A]], A => M[F[B]]
// turn A => M[F[B]] into M[A => F[B]] 
//   using _.flatMap(f) = M[A] => M[F[B]]
//   using applicative ?? <- this assumes we have knowledge of the function, no? i.e. we know return type is M[_]
// now have M[F[A]], M[A => F[B]] <- that's almost an applicative
// turn M[A => F[B]] into M[F[A] => F[B]]
//  using _.flatMap(f) on A => F[B]
//  M[A => F[B]] map (x => _.flatMap(x))
// now have M[F[A]], M[F[A] => F[B]] <- that's an applicative

//NOW GENERALIZE THE ABOVE TO ARBITRARY MAPPINGS!!

trait TransformerB[MA, Function]{
	type Out

	def apply(ma: MA, f: Function): Out
}

object TransformerB extends LowPriorityTransformerB {
	def apply[MA, Function](implicit tm: TransformerB[MA, Function]): Aux[MA, Function, tm.Out] = tm

	implicit def recur[F[_], G, Function](implicit bind: Bind[F], tb: TransformerB[G, Function]): Aux[F[G], Function, F[tb.Out]] =
		new TransformerB[F[G], Function]{
			type Out = F[tb.Out]

			def apply(fg: F[G], f: Function) ={
				???
			}
		}
}

trait LowPriorityTransformerB {
	type Aux[MA, Function, Out0] = TransformerB[MA, Function]{ type Out = Out0 }

	implicit def base[F[_], A, C >: A, B](implicit bind: Bind[F]): Aux[F[A], C => F[B], F[B]] =
		new TransformerB[F[A], C => F[B]]{
			type Out = F[B]

			def apply(fa: F[A], f: C => F[B]) = bind.bind(fa)(f)
		}
}