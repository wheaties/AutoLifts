package autolift

import scalaz.Functor

object AutoLift{
	implicit class MapperOps[F[_] : Functor, A](fa: F[A]){
		def liftMap[Function](f: Function)(implicit mapper: Mapper[F[A], Function]): mapper.Out = mapper(fa, f)

		def liftAp[Function](f: Function)(implicit ap: Ap[F[A], Function]): ap.Out = ap(fa, f)

		def liftFlatMap[Function](f: Function)(implicit fm: FlatMapper[F[A], Function]): fm.Out = fm(fa, f)
	}
}