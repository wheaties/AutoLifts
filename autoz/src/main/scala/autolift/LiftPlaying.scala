package autolift

import scalaz.Bind


/*
  Obj1..22 = Go up to arity
  Need `arity + 1` DFunctions

  A, B .. 22 = Go up to arity
    - Monad wrapped => M[A], M[B]...
    - Also need `arity + 1` out type (C)
  A1, B1 .. 22 = Go up to arity
    - Type bounds on A B etc
    - Tupled (A1, B1 ..)
    -
 */


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
//trait LiftM1[Obj0, Obj1, Function] extends DFunction3[Obj0, Obj1, Function]
//
//object LiftM1 extends LowPriorityLiftM1 {
//  def apply[Obj0, Obj1, Fn](implicit lift: LiftM1[Obj0, Obj1, Fn]): Aux[Obj0, Obj1, Fn, lift.Out] = lift
//
//  implicit def base[M[_], A0, A1, AA0 >: A0, AA1 >: A1, C](implicit bind: Bind[M]): Aux[M[A0], M[A1], (AA0, AA1) => C, M[C]] =
//    new LiftM1[M[A0], M[A1], (AA0, AA1) => C] {
//      type Out = M[C]
//
//      def apply(ma0: M[A0], ma1: M[A1], f: (AA0, AA1) => C) = bind.bind(ma0) { a0: A0 =>
//        bind.map(ma1) { a1: A1 => f(a0, a1) }
//      }
//    }
//}
//
//trait LowPriorityLiftM1 {
//  type Aux[Obj0, Obj1, Fn, Out0] = LiftM1[Obj0, Obj1, Fn] {type Out = Out0}
//
//  implicit def recur[M[_], A0, A1, Fn](implicit bind: Bind[M], lift: LiftM1[A0, A1, Fn]): Aux[M[A0], M[A1], Fn, M[lift.Out]] =
//    new LiftM1[M[A0], M[A1], Fn] {
//      type Out = M[lift.Out]
//
//      def apply(ma0: M[A0], ma1: M[A1], f: Fn) = bind.bind(ma0) { a0: A0 =>
//        bind.map(ma1) { a1: A1 => lift(a0, a1, f) }
//      }
//    }
//}

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
//trait LiftM3[Obj1, Obj2, Obj3, Function] extends DFunction4[Obj1, Obj2, Obj3, Function]
//
//object LiftM3 extends LowPriorityLiftM3 {
//  def apply[Obj1, Obj2, Obj3, Fn](implicit lift: LiftM3[Obj1, Obj2, Obj3, Fn]): Aux[Obj1, Obj2, Obj3, Fn, lift.Out] = lift
//
//  implicit def base[M[_], A, B, C, A1 >: A, B1 >: B, C1 >: C, D]
//  (implicit bind: Bind[M]): Aux[M[A], M[B], M[C], (A1, B1, C1) => D, M[D]] =
//    new LiftM3[M[A], M[B], M[C], (A1, B1, C1) => D] {
//      type Out = M[D]
//
//      def apply(ma: M[A], mb: M[B], mc: M[C], f: (A1, B1, C1) => D) = bind.bind(ma) { a: A =>
//        bind.bind(mb) { b: B =>
//          bind.map(mc) { c: C => f(a, b, c) }
//        }
//      }
//    }
//}
//
//trait LowPriorityLiftM3 {
//  type Aux[Obj1, Obj2, Obj3, Fn, Out0] = LiftM3[Obj1, Obj2, Obj3, Fn] {type Out = Out0}
//
//  implicit def recur[M[_], A, B, C, Fn]
//  (implicit bind: Bind[M], lift: LiftM3[A, B, C, Fn]): Aux[M[A], M[B], M[C], Fn, M[lift.Out]] =
//    new LiftM3[M[A], M[B], M[C], Fn] {
//      type Out = M[lift.Out]
//
//      def apply(ma: M[A], mb: M[B], mc: M[C], f: Fn) = bind.bind(ma) { a: A =>
//        bind.bind(mb) { b: B =>
//          bind.map(mc) { c: C => lift(a, b, c, f) }
//        }
//      }
//    }
//}

//object Foo {
//  def liftM2[A1, A2, C](f: (A1, A2) => C) = new LiftedM2(f)
//
//  sealed class LiftedM2[A1, A2, C](f: (A1, A2) => C) {
//    def map[D](g: C => D): LiftedM2[A1, A2, D] = new LiftedM2((x: A1, y: A2) => g(f(x, y)))
//
//    def apply[AA1, AA2](aa1: AA1, aa2: AA2)(implicit lift: LiftM2[AA1, AA2, (A1, A2) => C]): lift.Out = lift(aa1, aa2, f)
//  }
//
//  def liftM3[A, B, C, D](f: (A, B, C) => D) = new LiftedM3(f)
//
//  sealed class LiftedM3[A, B, C, D](f: (A, B, C) => D){
//    def map[E](g: D => E): LiftedM3[A, B, C, E] = new LiftedM3((x: A, y: B, z: C) => g(f(x, y, z)))
//
//    def apply[MA, MB, MC](ma: MA, mb: MB, mc: MC)(implicit lift: LiftM3[MA, MB, MC, (A, B, C) => D]): lift.Out =
//      lift(ma, mb, mc, f)
//  }
//}
