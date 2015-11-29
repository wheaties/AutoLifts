import sbt._

/**
 * Copied, with some modifications, from https://github.com/milessabin/shapeless/blob/master/project/Boilerplate.scala
 *
 * Generate a range of boilerplate classes, those offering alternatives with 0-22 params
 * and would be tedious to craft by hand
 *
 * @author Miles Sabin
 * @author Kevin Wright
 */


object Boilerplate {

  import scala.StringContext._

  implicit final class BlockHelper(val sc: StringContext) extends AnyVal {
    def block(args: Any*): String = {
      val interpolated = sc.standardInterpolator(treatEscapes, args)
      val rawLines = interpolated split '\n'
      val trimmedLines = rawLines map {
        _ dropWhile (_.isWhitespace)
      }
      trimmedLines mkString "\n"
    }
  }


  val templates: Seq[Template] = Seq(
    GenDFunctions,
    GenLifterInstances,
    GenLifterMFunctions
  )

  val header = "// auto-generated boilerplate" // TODO: put something meaningful here?


  /** Returns a seq of the generated files.  As a side-effect, it actually generates them... */
  def gen(dir: File) = for (t <- templates) yield {
    val tgtFile = t.filename(dir)
    IO.write(tgtFile, t.body)
    tgtFile
  }

  //TODO: Should be 22. keep at 2 for now to test
  val maxArity = 22

//  final class TemplateVals(val arity: Int) {
//    val synTypes = (0 until arity) map (n => s"A$n")
//    val synVals = (0 until arity) map (n => s"a$n")
//    val synTypedVals = (synVals zip synTypes) map { case (v, t) => v + ":" + t }
//    val `A..N` = synTypes.mkString(", ")
//    val `a..n` = synVals.mkString(", ")
//    val `_.._` = Seq.fill(arity)("_").mkString(", ")
//    val `(A..N)` = if (arity == 1) "Tuple1[A]" else synTypes.mkString("(", ", ", ")")
//    val `(_.._)` = if (arity == 1) "Tuple1[_]" else Seq.fill(arity)("_").mkString("(", ", ", ")")
//    val `(a..n)` = if (arity == 1) "Tuple1(a)" else synVals.mkString("(", ", ", ")")
//    val `a:A..n:N` = synTypedVals mkString ", "
//  }

  final class TemplateVals(val arity: Int, prefix: String = "A") {
    private val a = prefix.toLowerCase

    val synTypes = (0 until arity) map (n => s"$prefix$n")
    val synVals = (0 until arity) map (n => s"$a$n")
    val synTypedVals = (synVals zip synTypes) map { case (v, t) => v + ":" + t }
    val `A..N` = synTypes.mkString(", ")
    val `a..n` = synVals.mkString(", ")
    val `_.._` = Seq.fill(arity)("_").mkString(", ")
    val `(A..N)` = if (arity == 1) s"Tuple1[$prefix]" else synTypes.mkString("(", ", ", ")")
    val `(_.._)` = if (arity == 1) "Tuple1[_]" else Seq.fill(arity)("_").mkString("(", ", ", ")")
    val `(a..n)` = if (arity == 1) s"Tuple1($a)" else synVals.mkString("(", ", ", ")")
    val `a:A..n:N` = synTypedVals mkString ", "
  }

  trait Template {
    def filename(root: File): File
    def content(tv: TemplateVals): String
    def range = 1 to maxArity
    def body: String = {
      val headerLines = header split '\n'
      val rawContents = range map { n => content(new TemplateVals(n)) split '\n' filterNot (_.isEmpty) }
      val preBody = rawContents.head takeWhile (_ startsWith "|") map (_.tail)
      val instances = rawContents flatMap {
        _ filter (_ startsWith "-") map (_.tail)
      }
      val postBody = rawContents.head dropWhile (_ startsWith "|") dropWhile (_ startsWith "-") map (_.tail)
      (headerLines ++ preBody ++ instances ++ postBody) mkString "\n"
    }
  }

  /*
    Blocks in the templates below use a custom interpolator, combined with post-processing to produce the body
      - The contents of the `header` val is output first
      - Then the first block of lines beginning with '|'
      - Then the block of lines beginning with '-' is replicated once for each arity,
        with the `templateVals` already pre-populated with relevant relevant vals for that arity
      - Then the last block of lines prefixed with '|'
    The block otherwise behaves as a standard interpolated string with regards to variable substitution.
  */

  object GenDFunctions extends Template {
    override def filename(root: File): File = root / "autolift" / "Functions.scala"

    override def content(tv: TemplateVals): String = {
      import tv._

      block"""
        |package autolift
        |
        - trait DFunction$arity[${`A..N`}]{
        -   type Out
        -   def apply(${`a:A..n:N`}): Out
        -
        -   override def toString() = "<DFunction$arity>"
        -}
      """
    }
  }

  object GenLifterMFunctions extends Template {
    override def filename(root: File): File = root / "autolift" / "LifterMFunctions.scala"

    /**
     * Need one less than actual max because need enough DFunctions.
     * @return
     */
    override def range = 2 to (maxArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv.arity

      val aTempVals = new TemplateVals(arity, "A")
      val aaTempVals = new TemplateVals(arity, "AA")

      block"""
        |package autolift
        |
        |trait LiftMFunctions {
        |
        -
        -  def liftM$arity[${aTempVals.`A..N`}, C](f: (${aTempVals.`A..N`}) => C) = new LiftedM$arity(f)
        -
        -  sealed class LiftedM$arity[${aTempVals.`A..N`}, C](f: (${aTempVals.`A..N`}) => C) {
        -    def map[D](g: C => D): LiftedM$arity[${aTempVals.`A..N`}, D] = new LiftedM$arity((${aTempVals.`a:A..n:N`}) => g(f(${aTempVals.`a..n`})))
        -
        -    def apply[${aaTempVals.`A..N`}](${aaTempVals.`a:A..n:N`})(implicit lift: LiftM$arity[${aaTempVals.`A..N`}, (${aTempVals.`A..N`}) => C]): lift.Out =
        -      lift(${aaTempVals.`a..n`}, f)
        -  }
        |}
      """
    }
  }

  object GenLifterInstances extends Template {
    override def filename(root: File): File = root / "autolift" / "LiftersGen.scala"

    /**
     * Need one less than actual max because need enough DFunctions.
     * @return
     */
    override def range = 2 to (maxArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv.arity

      val obj = new TemplateVals(arity, "Obj")

      val aTempVals = new TemplateVals(arity, "A")
      val aaTempVals = new TemplateVals(arity, "AA")

      val `AA >: A .. N` = (aaTempVals.synTypes zip aTempVals.synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")

      val aMonadTypes = aTempVals.synTypes.map(t => s"M[$t]")
      val aMonadTypeVals = aTempVals.synVals.map(t => s"m$t")

      val `M[A]..M[N]` = aMonadTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((aMonadTypeVals zip aMonadTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")


      val applyBinds = (aTempVals.synVals zip aMonadTypeVals zip aTempVals.synTypes) map {case ((ttv, mtv), s) =>
        s"bind.bind($mtv) { $ttv : $s => "
      }



      def applyBind(inner: String) = {
        val (all, last) = (aTempVals.synVals zip aMonadTypeVals zip aTempVals.synTypes).splitAt(aMonadTypeVals.size - 1)

        val innerA = last.headOption.map{ case ((ttv, mtv), s) => s"bind.map($mtv) { $ttv: $s => $inner }"}.getOrElse("")

        all.foldRight(innerA) { case (((ttv, mtv), s), res) =>
         s"bind.bind($mtv) { $ttv: $s => $res }"
        }
      }


      val regularApplyBind = applyBind(s"f(${aTempVals.`a..n`})")
      val lowpriotiryApplyBind = applyBind(s"lift(${aTempVals.`a..n`}, f)")


      block"""
        |package autolift
        |
        |import scalaz.Bind
        |
        -
        -trait LiftM$arity[${obj.`A..N`}, Function] extends DFunction${arity + 1}[${obj.`A..N`}, Function]
        -
        -object LiftM$arity extends LowPriorityLiftM$arity {
        -  def apply[${obj.`A..N`}, Fn](implicit lift: LiftM$arity[${obj.`A..N`}, Fn]): Aux[${obj.`A..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${aTempVals.`A..N`}, ${`AA >: A .. N`}, C](implicit bind: Bind[M]): Aux[${`M[A]..M[N]`}, ${aaTempVals.`(A..N)`} => C, M[C]] =
        -    new LiftM$arity[${`M[A]..M[N]`}, ${aaTempVals.`(A..N)`} => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: ${aaTempVals.`(A..N)`} => C) = $regularApplyBind
        -    }
        - }
        -
        -trait LowPriorityLiftM$arity {
        -  type Aux[${obj.`A..N`}, Fn, Out0] = LiftM$arity[${obj.`A..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${aTempVals.`A..N`}, Fn](implicit bind: Bind[M], lift: LiftM$arity[${aTempVals.`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new LiftM$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowpriotiryApplyBind
        -    }
        -}
      """
    }
  }

  /*
  object GenApplyBuilders extends Template {
    def filename(root: File) = root /  "cats" / "syntax" / "ApplyBuilder.scala"

    def content(tv: TemplateVals) = {
      import tv._

      val tpes = synTypes map { tpe => s"F[$tpe]" }
      val tpesString = synTypes mkString ", "
      val params = (synVals zip tpes) map { case (v,t) => s"$v:$t"} mkString ", "
      val next = if (arity + 1 <= maxArity) {
        s"def |@|[Z](z: F[Z]) = new ApplyBuilder${arity + 1}(${`a..n`}, z)"
      } else {
        ""
      }

      val n = if (arity == 1) { "" } else { arity.toString }

      val tupled = if (arity != 1) {
        s"def tupled(implicit F: Apply[F]): F[(${`A..N`})] = F.tuple$n(${`a..n`})"
      } else {
        ""
      }

      block"""
         |package autolit
         |
        |private[syntax] final class ApplyBuilder[F[_]] {
         |  def |@|[A](a: F[A]) = new ApplyBuilder1(a)
         |
        -  private[syntax] final class ApplyBuilder$arity[${`A..N`}](${params}) {
        -    $next
        -    def ap[Z](f: F[(${`A..N`}) => Z])(implicit F: Apply[F]): F[Z] = F.ap$n(${`a..n`})(f)
        -    def map[Z](f: (${`A..N`}) => Z)(implicit F: Apply[F]): F[Z] = F.map$n(${`a..n`})(f)
        -    $tupled
        - }
         |}
      """
    }
  }

  object GenApplyArityFunctions extends Template {
    def filename(root: File) = root / "cats" / "ApplyArityFunctions.scala"
    override def range = 3 to maxArity
    def content(tv: TemplateVals) = {
      import tv._

      val tpes = synTypes map { tpe => s"F[$tpe]" }
      val tpesString = synTypes mkString ", "
      val fargs = (0 until arity) map { "f" + _ }
      val fargsS = fargs mkString ", "
      val fparams = (fargs zip tpes) map { case (v,t) => s"$v:$t"} mkString ", "

      val a = arity / 2
      val b = arity - a

      val fArgsA = (0 until a) map { "f" + _ } mkString ","
      val fArgsB = (a until arity) map { "f" + _ } mkString ","
      val argsA = (0 until a) map { "a" + _ } mkString ","
      val argsB = (a until arity) map { "a" + _ } mkString ","
      def apN(n: Int) = if (n == 1) { "ap" } else { s"ap$n" }
      def allArgs = (0 until arity) map { "a" + _ } mkString ","

      val map = if (arity == 3) {
        " ap(f2)(map2(f0, f1)((a, b) => c => f(a, b, c)))"
      }  else {
        block"""
          -    map2(tuple$a($fArgsA), tuple$b($fArgsB)) {
          -      case (($argsA), ($argsB)) => f($allArgs)
          -    }
        """
      }
      val apply =
        block"""
          -    ${apN(b)}($fArgsB)(${apN(a)}($fArgsA)(map(f)(f =>
          -      ($argsA) => ($argsB) => f($allArgs)
          -    )))
          """

      block"""
         |package cats
         |trait ApplyArityFunctions[F[_]] { self: Apply[F] =>
         |  def tuple2[A, B](fa: F[A], fb: F[B]): F[(A, B)] = map2(fa, fb)((_, _))
         |
        -  def ap$arity[${`A..N`}, Z]($fparams)(f: F[(${`A..N`}) => Z]):F[Z] = $apply
        -  def map$arity[${`A..N`}, Z]($fparams)(f: (${`A..N`}) => Z):F[Z] = $map
        -  def tuple$arity[${`A..N`}]($fparams):F[(${`A..N`})] =
        -    map$arity($fargsS)((${`_.._`}))
         |}
      """
    }
  }
  */

}
