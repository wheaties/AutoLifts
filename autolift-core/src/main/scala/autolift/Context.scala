package autolift


final class LiftedM2[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedM2[A, B, D] = new LiftedM2((x: A, y: B) => g(f(x, y)))

	def apply[MA, MB](ma: MA, mb: MB)(implicit lift: LiftM2[MA, MB, (A, B) => C]): lift.Out = lift(ma, mb, f)
}

final class LiftedM3[A, B, C, D](f: (A, B, C) => D){
	def map[E](g: D => E): LiftedM3[A, B, C, E] = new LiftedM3((x: A, y: B, z: C) => g(f(x, y, z)))

	def apply[MA, MB, MC](ma: MA, mb: MB, mc: MC)(implicit lift: LiftM3[MA, MB, MC, (A, B, C) => D]): lift.Out = 
		lift(ma, mb, mc, f)
}

trait LiftMContexts{
	def liftM2[A, B, C](f: (A, B) => C) = new LiftedM2(f)

	def liftM3[A, B, C, D](f: (A, B, C) => D) = new LiftedM3(f)
}

final class LiftedA2[A, B, C](f: (A, B) => C){
	def map[D](g: C => D): LiftedA2[A, B, D] = new LiftedA2((x: A, y: B) => g(f(x, y)))

	def apply[MA, MB](ma: MA, mb: MB)(implicit lift: LiftA2[MA, MB, (A, B) => C]): lift.Out = lift(ma, mb, f)
}

final class LiftedA3[A, B, C, D](f: (A, B, C) => D){
	def map[E](g: D => E): LiftedA3[A, B, C, E] = new LiftedA3((x: A, y: B, z: C) => g(f(x, y, z)))

	def apply[MA, MB, MC](ma: MA, mb: MB, mc: MC)(implicit lift: LiftA3[MA, MB, MC, (A, B, C) => D]): lift.Out = 
		lift(ma, mb, mc, f)
}

trait LiftAContexts{
	def liftA2[A, B, C](f: (A, B) => C) = new LiftedA2(f)

	def liftA3[A, B, C, D](f: (A, B, C) => D) = new LiftedA3(f)
}