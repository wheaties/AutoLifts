package autolift.cats

class LiftFoldLeftTest extends BaseSpec{
	"liftFoldLeft on a List" should "work" in{
		val in = List(1, 2, 3)
		val out = in.liftFoldLeft(0)(intintF _)

		same[Int](out, 6)
	}

	"liftFoldLeft on a List[Option]" should "work" in{
		val in = List(Option(1), Option(2), None)
		val out = in.liftFoldLeft(3)(intintF _)

		same[List[Int]](out, List(4, 5, 3))
	}
}