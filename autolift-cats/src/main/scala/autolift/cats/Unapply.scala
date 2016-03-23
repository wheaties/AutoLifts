package autolift.cats

import cats.Unapply

protected[cats] object Un{
  type Apply[TC[_[_]], MA, A0] = Unapply[TC, MA]{ type A = A0 }
}