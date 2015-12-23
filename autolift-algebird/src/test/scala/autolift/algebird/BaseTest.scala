package autolift.algebird

import org.scalatest.FlatSpec
import com.twitter.algebird.{Monad, Functor}

case class Foo[A](a: A)
object Foo{
	implicit val bind = new Monad[Foo]{
		def apply[T](t: T) = Foo(t)
		def flatMap[T, U](foo: Foo[T])(fn: T => Foo[U]) = fn(foo.a)
	}
}

case class Bar[A](a: A)
object Bar{
	implicit val fun = new Functor[Bar]{
		def map[T, U](m: Bar[T])(fn: T => U): Bar[U] = Bar(fn(m.a))
	}
}

trait BaseSpec extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)
}