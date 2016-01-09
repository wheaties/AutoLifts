package autolift.cats

class FoldCompleteTest extends BaseSpec{
	"foldComplete on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.foldComplete

		same[Int](out, 6)
	}

	"foldComplete on a List[Option]" should "work" in{
		val in = List(Option(1), Option(2), None)
		val out = in.foldComplete

		same[Option[Int]](out, Option(3))
	}

	"foldComplete on a List[List]" should "work on the List if the type A is not a Monoid" in{
		val in = List(List(1, "2"), List("3", 4))
		val out = in.foldComplete

		same[List[Any]](out, List(1, "2", "3", 4))
	}
}