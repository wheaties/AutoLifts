package autolift

/**
 * Type class supporting lifting a reverse on a Traverseable over an arbitrary nesting of 
 * type constructors.
 *
 * @author Owein Reese
 *
 * @tparam M The type at which to Reverse.
 * @tparam Obj The type over which to lift the reverse.
 */
trait LiftReverse[F[_], Obj] extends (Obj => Obj)

trait LiftReverseSyntax{

  /// Syntax extension providing for a `liftReverse` method.
  implicit class LiftReverseOps[F[_], A](fa: F[A]){

    /**
     * Automatic lifting of a Reverse on the first nested type matching `M`.
     */
    def liftReverse[M[_]](implicit lift: LiftReverse[M, F[A]]): F[A] = lift(fa)
  }
}

//Contexts for a liftReverse do not exist.