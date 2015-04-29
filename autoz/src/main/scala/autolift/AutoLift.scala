package autolift

import scalaz._

object AutoLift{
	implicit class MapperOps[F[_], A](fa: F[A])(implicit ev: Functor[F]){
		def liftMap[Function](f: Function)(implicit mapper: Mapper[F[A], Function]): mapper.Out = mapper(fa, f)

		def liftAp[Function](f: Function)(implicit ap: Ap[F[A], Function]): ap.Out = ap(fa, f)
	}

	implicit class FlatMapperOps[F[_], A](fa: F[A])(implicit ev: Functor[F]){
		def liftFlatMap[Function](f: Function)(implicit fm: FlatMapper[F[A], Function]): fm.Out = fm(fa, f)
	}
}