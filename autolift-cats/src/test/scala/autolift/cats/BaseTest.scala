package autolift.cats

import org.scalatest.FlatSpec

//TODO: This and the one from ScalaZ package should prob be moved up a level to Core
trait BaseSpec extends FlatSpec{
	def same[A](x: A, y: A) = assert(x == y)

	val s2i = {x: String => x.toInt }
	val intF = {x: Int => x+1}
	def intintF(x: Int, y: Int) = x + y
	def anyanyF(x: Any, y: Any) = 1
	val intL = {x: Int => List(x+1, x+2)}
	val intAp = List(intF)
	val anyF = {x: Any => 1}
	val anyL = {x: Any => List(1)}
	val anyO = { x: Any => Option(1) }
	val even = {x: Int => x % 2 == 0 }
	val any = {x: Any => x.hashCode % 2 == 0}
}