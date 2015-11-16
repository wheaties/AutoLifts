package autolift

import export._

/**
 * Type class supporting foldRight over an arbitrary nesting of type constructors given an initial value and a function.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type to be lifted into.
 * @tparam Function The 2-airy function to be lifted.
 * @tparam Z The initial value of the fold.
 */
trait LiftFoldRight[FA, Function, Z] extends DFunction3[FA, Function, Z]

@imports[LiftFoldRight]
object LiftFoldRight

//See individual instances for both Syntax and Contexts of liftFoldRight.