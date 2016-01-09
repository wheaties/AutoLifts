package autolift.cats

class FoldAnyTest extends BaseSpec{
	"foldAny on a List" should "work" in{
		val in = List(1, 2, 3)
		
		assert(in.foldAny{x: Int => x < 2})
		assert(!in.foldAny{x: Int => x < 0})
	}

	"foldAny on a List" should "work with functions" in{
		val in = List(1, 2, 3)
		
		assert(in.foldAny{x: Any => true})
		assert(!in.foldAny{x: Any => false})
	}

	"foldAny on a List[Option]" should "work" in{
		val in = List(Option(1), None, Option(2))
		
		assert(in.foldAny{x: Int => x < 2})
		assert(!in.foldAny{x: Int => x < 0})
	}
}