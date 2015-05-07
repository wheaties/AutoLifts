package autolift

import scalaz._

trait Apart[FA]{
	type F[X]
	type A

	def apply(fa: FA): F[A]
}

object Apart{
	def apply[FA](implicit apart: Apart[FA]): Aux[FA, apart.F, apart.A] = apart

	type Aux[FA, F0[_], A0] = Apart[FA]{ type F[X] = F0[X]; type A = A0 }

	implicit def mk[F0[_], A0]: Aux[F0[A0], F0, A0] =
		new Apart[F0[A0]]{
			type F[X] = F0[X]
			type A = A0

			def apply(fa: F0[A0]) = fa
		}
}

trait TransformerMap[FA, Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object TransformerMap extends LowPriorityTransformerMap{
	def apply[FA, Function](implicit tm: TransformerMap[FA, Function]): Aux[FA, Function, tm.Out] = tm

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], 
		fm: TransformerMap[G, Function]): Aux[F[G], Function, F[fm.Out]] =
		new TransformerMap[F[G], Function]{
			type Out = F[fm.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => fm(g, f) }
		}
}

trait LowPriorityTransformerMap{
	type Aux[FA, Function, Out0] = TransformerMap[FA, Function]{ type Out = Out0 }

	//As the name suggests, forces compilation to only be allowed if the inner most Functor can be acted upon.
	implicit def fail[F[_], GA, G[_], A, B](implicit ap: Apart.Aux[GA, G, A],
		f1: Functor[F], 
		f2: Functor[G]): Aux[F[G[A]], G[A] => B, F[B]] = ???

	implicit def base[F[_], A, B](implicit functor: Functor[F]): Aux[F[A], A => B, F[B]] =
		new TransformerMap[F[A], A => B]{
			type Out = F[B]

			def apply(fa: F[A], f: A => B) = functor.map(fa)(f)
		}
}

trait TransformerFlatMap[FA, Function]{
	type Out

	def apply(fa: FA, f: Function): Out
}

object TransformerFlatMap extends LowPriorityTransformerFlatMap{
	def apply[FA, Function](implicit tm: TransformerFlatMap[FA, Function]): Aux[FA, Function, tm.Out] = tm

	implicit def recur[F[_], G, Function](implicit functor: Functor[F], 
		fm: TransformerFlatMap[G, Function]): Aux[F[G], Function, F[fm.Out]] =
		new TransformerFlatMap[F[G], Function]{
			type Out = F[fm.Out]

			def apply(fg: F[G], f: Function) = functor.map(fg){ g: G => fm(g, f) }
		}
}

trait LowPriorityTransformerFlatMap{
	type Aux[FA, Function, Out0] = TransformerFlatMap[FA, Function]{ type Out = Out0 }

	//As the name suggests, forces compilation to only be allowed if the inner most Bind can be acted upon.
	implicit def fail[F[_], GA, G[_], A, B](implicit ap: Apart.Aux[GA, G, A],
		f1: Bind[F], 
		f2: Bind[G]): Aux[F[GA], GA => F[B], F[B]] = ???

	implicit def base[F[_], A, B](implicit bind: Bind[F]): Aux[F[A], A => F[B], F[B]] =
		new TransformerFlatMap[F[A], A => F[B]]{
			type Out = F[B]

			def apply(fa: F[A], f: A => F[B]) = bind.bind(fa)(f)
		}
}