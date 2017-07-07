package autolift


/**
 * Type class supporting the applicative mapping of a type over another type of arbitrary nested type constructors.
 *
 * @author Owein Reese
 *
 * @tparam Obj The type of object to be lifted into.
 * @tparam Funciton The type of function to be lifted.
 */
trait LiftAp[Obj, Function] extends DFunction2[Obj, Function]


trait LiftApSyntax{

  /// Syntax extension providing for a `liftAp` method.
  implicit class LiftApOps[F[_], A](fa: F[A]){

    /**
     * Automatic Applicative lifting of the contained function `f` such that the application point is dictated by the
     * type of the Applicative.
     *
     * @param f the wrapped function to be lifted.
     * @tparam B the argument type of the function.
     * @tparam C the return type of the function.
     * @tparam M the higher-kinded type with an Applicative.
     */
    def liftAp[B, C, M[_]](f: M[B => C])(implicit lift: LiftAp[F[A], M[B => C]]): lift.Out = lift(fa, f)
  }
}

//See individual implementations for liftAp Context.

