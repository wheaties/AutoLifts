package autolift

import scalaz.Bind


/**
 * Typeclass supporting lifting a function of airity 2 and applying it to the inner types through a combination of
 * flatMap and map.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The first object over which to apply the function.
 * @tparam Obj2 The second object over which to apply the function.
 * @tparam Function The function to be used to map values.
 */
trait LiftM2[Obj1, Obj2, Function] extends DFunction3[Obj1, Obj2, Function]

object LiftM2 extends LowPriorityLiftM2 {
  def apply[Obj1, Obj2, Fn](implicit lift: LiftM2[Obj1, Obj2, Fn]): Aux[Obj1, Obj2, Fn, lift.Out] = lift

  implicit def base[M[_], A, B, A1 >: A, B1 >: B, C](implicit bind: Bind[M]): Aux[M[A], M[B], (A1, B1) => C, M[C]] =
    new LiftM2[M[A], M[B], (A1, B1) => C] {
      type Out = M[C]

      def apply(ma: M[A], mb: M[B], f: (A1, B1) => C) = bind.bind(ma) { a: A =>
        bind.map(mb) { b: B => f(a, b) }
      }
    }
}

trait LowPriorityLiftM2 {
  type Aux[Obj1, Obj2, Fn, Out0] = LiftM2[Obj1, Obj2, Fn] {type Out = Out0}

  implicit def recur[M[_], A, B, Fn](implicit bind: Bind[M], lift: LiftM2[A, B, Fn]): Aux[M[A], M[B], Fn, M[lift.Out]] =
    new LiftM2[M[A], M[B], Fn] {
      type Out = M[lift.Out]

      def apply(ma: M[A], mb: M[B], f: Fn) = bind.bind(ma) { a: A =>
        bind.map(mb) { b: B => lift(a, b, f) }
      }
    }
}

/**
 * Typeclass supporting lifting a function of airity 3 and applying it to the inner types through a combination of
 * flatMap and map.
 *
 * @author Owein Reese
 *
 * @tparam Obj1 The first object over which to apply the function.
 * @tparam Obj2 The second object over which to apply the function.
 * @tparam Obj3 The third object over which to apply the function.
 * @tparam Function The function to be used to map values.
 */
trait LiftM3[Obj1, Obj2, Obj3, Function] extends DFunction4[Obj1, Obj2, Obj3, Function]

object LiftM3 extends LowPriorityLiftM3 {
  def apply[Obj1, Obj2, Obj3, Fn](implicit lift: LiftM3[Obj1, Obj2, Obj3, Fn]): Aux[Obj1, Obj2, Obj3, Fn, lift.Out] = lift

  implicit def base[M[_], A, B, C, A1 >: A, B1 >: B, C1 >: C, D]
  (implicit bind: Bind[M]): Aux[M[A], M[B], M[C], (A1, B1, C1) => D, M[D]] =
    new LiftM3[M[A], M[B], M[C], (A1, B1, C1) => D] {
      type Out = M[D]

      def apply(ma: M[A], mb: M[B], mc: M[C], f: (A1, B1, C1) => D) = bind.bind(ma) { a: A =>
        bind.bind(mb) { b: B =>
          bind.map(mc) { c: C => f(a, b, c) }
        }
      }
    }
}

trait LowPriorityLiftM3 {
  type Aux[Obj1, Obj2, Obj3, Fn, Out0] = LiftM3[Obj1, Obj2, Obj3, Fn] {type Out = Out0}

  implicit def recur[M[_], A, B, C, Fn]
  (implicit bind: Bind[M], lift: LiftM3[A, B, C, Fn]): Aux[M[A], M[B], M[C], Fn, M[lift.Out]] =
    new LiftM3[M[A], M[B], M[C], Fn] {
      type Out = M[lift.Out]

      def apply(ma: M[A], mb: M[B], mc: M[C], f: Fn) = bind.bind(ma) { a: A =>
        bind.bind(mb) { b: B =>
          bind.map(mc) { c: C => lift(a, b, c, f) }
        }
      }
    }
}

