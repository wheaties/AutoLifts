package autolift.cats

import cats.Unapply

protected[cats] object Un{
	type Apply[TC[_[_]], MA, A0] = Unapply[TC, MA]{ type A = A0 }

	type Aux[TC[_[_]], MA, M0[_], A0] = Unapply[TC, MA]{ type M[X] = M0[X]; type A = A0 }
}