package autolift.scalaz

import scalaz.{Unapply, UnapplyProduct}

protected[scalaz] object Un{
  type Apply[TC[_[_]], MA, A0] = Unapply[TC, MA]{ type A = A0 }
  type Apply2[TC[_[_]], MA, MB, A0, B0] = UnapplyProduct[TC, MA, MB]{ type A = A0; type B = B0 }
}