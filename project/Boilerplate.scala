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


  val coreTemplates: Seq[Template] = Seq(
    GenDFunctions,
    GenCoreLiftM,
    GenCoreLiftA
  )

  def genCore(dir: File) = gen(dir)(coreTemplates)

  val scalazTemplates = Seq(
    GenScalazLiftMInstances,
    GenScalazLiftAInstances
  )

  def genScalaz(dir: File) = gen(dir)(scalazTemplates)

  val algebirdTemplates = Seq(
    GenAlgebirdLiftMInstances,
    GenAlgebirdLiftAInstances)

  def genAlgebird(dir: File) = gen(dir)(algebirdTemplates)

  val catsTemplates = Seq(
    GenCatsLiftMInstances,
    GenCatsLiftAInstances
  )

  def genCats(dir: File) = gen(dir)(catsTemplates)

  val header = "// auto-generated boilerplate" // TODO: put something meaningful here?

  /** Returns a seq of the generated files.  As a side-effect, it actually generates them... */
  def gen(dir: File)(templates: Seq[Template]) = for (t <- templates) yield {
    val tgtFile = t.filename(dir)
    IO.write(tgtFile, t.body)
    tgtFile
  }

  val maxArity = 22
  val maxExportedArity = 22

  final class TemplateVals(val arity: Int, prefix: String = "A") {
    private val a = prefix.toLowerCase

    val synTypes = (0 until arity) map (n => s"$prefix$n")
    val synVals = (0 until arity) map (n => s"$a$n")
    val synTypedVals = (synVals zip synTypes) map { case (v, t) => v + ":" + t }
    val `A..N` = synTypes mkString ", "
    val `a..n` = synVals mkString ", "
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
        -trait DFunction$arity[${`A..N`}]{
        -  type Out
        -  def apply(${`a:A..n:N`}): Out
        -
        -  override def toString() = "<DFunction$arity>"
        -}
      """
    }
  }

  object GenCoreLiftM extends Template{
    override def filename(root: File): File = root / "autolift" / "LiftMGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")
      val AA = new TemplateVals(arity, "AA")

      block"""
        |package autolift
        |
        |
        -
        -trait LiftM$arity[${`Obj..N`}, Fn] extends DFunction${arity + 1}[${`Obj..N`}, Fn]
        -
        -object LiftM$arity
        -
        -final class LiftedM$arity[${`A..N`}, C](f: (${`A..N`}) => C) {
        -  def map[D](g: C => D): LiftedM$arity[${`A..N`}, D] = new LiftedM$arity((${`a:A..n:N`}) => g(f(${`a..n`})))
        -
        -  def apply[${AA.`A..N`}](${AA.`a:A..n:N`})(implicit lift: LiftM$arity[${AA.`A..N`}, (${`A..N`}) => C]): lift.Out =
        -    lift(${AA.`a..n`}, f)
        -}
        -
        -trait LiftM${arity}Context{
        -  def liftM$arity[${`A..N`}, C](f: (${`A..N`}) => C) = new LiftedM$arity(f)
        -}
      """
    }
  }

  object GenCoreLiftA extends Template{
    override def filename(root: File): File = root / "autolift" / "LiftAGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")
      val AA = new TemplateVals(arity, "AA")

      block"""
        |package autolift
        |
        |
        -
        -trait LiftA$arity[${`Obj..N`}, Fn] extends DFunction${arity + 1}[${`Obj..N`}, Fn]
        -
        -object LiftA$arity
        -
        -final class LiftedA$arity[${`A..N`}, C](f: (${`A..N`}) => C) {
        -  def map[D](g: C => D): LiftedA$arity[${`A..N`}, D] = new LiftedA$arity((${`a:A..n:N`}) => g(f(${`a..n`})))
        -
        -  def apply[${AA.`A..N`}](${AA.`a:A..n:N`})(implicit lift: LiftA$arity[${AA.`A..N`}, (${`A..N`}) => C]): lift.Out =
        -    lift(${AA.`a..n`}, f)
        -}
        -
        -trait LiftA${arity}Context{
        -  def liftA$arity[${`A..N`}, C](f: (${`A..N`}) => C) = new LiftedA$arity(f)
        -}
      """
    }
  }

  object GenCatsLiftMInstances extends Template {
    override def filename(root: File): File = root / "autolift" / "cats" / "LiftersGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")

      val altSynTypes = (0 until arity) map (n => s"AA$n")

      val `AA..N` = altSynTypes.mkString(", ")

      val `AA >: A..N` = (altSynTypes zip synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")

      val synMTypes = synTypes.map(t => s"M[$t]")
      val synMVals = synVals.map(t => s"m$t")

      val `M[A]..M[N]` = synMTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((synMVals zip synMTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")

      def liftMApply(inner: String) = {
        val (all, last) = (synVals zip synMVals zip synTypes).splitAt(synMVals.size - 1)

        val innerBind = {
          val ((sv, smv), st) = last.head
          s"bind.map($smv) { $sv: $st => $inner }"
        }

        all.foldRight(innerBind) { case (((sv, smv), st), res) =>
          s"bind.flatMap($smv) { $sv: $st => $res }"
        }
      }

      val apply = liftMApply(s"f(${`a..n`})")
      val lowPriorityBind = liftMApply(s"lift(${`a..n`}, f)")


      block"""
         |package autolift.cats
         |
         |import cats.FlatMap
         |
        -import autolift.LiftM$arity
        -
        -trait CatsLiftM$arity[${`Obj..N`}, Fn] extends LiftM$arity[${`Obj..N`}, Fn]
        -
        -object CatsLiftM$arity extends LowPriorityCatsLiftM$arity {
        -  def apply[${`Obj..N`}, Fn](implicit lift: CatsLiftM$arity[${`Obj..N`}, Fn]): Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${`A..N`}, ${`AA >: A..N`}, C](implicit bind: FlatMap[M]): Aux[${`M[A]..M[N]`}, (${`AA..N`}) => C, M[C]] =
        -    new CatsLiftM$arity[${`M[A]..M[N]`}, (${`AA..N`}) => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: (${`AA..N`}) => C) = $apply
        -    }
        -}
        -
        -trait LowPriorityCatsLiftM$arity {
        -  type Aux[${`Obj..N`}, Fn, Out0] = CatsLiftM$arity[${`Obj..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${`A..N`}, Fn](implicit bind: FlatMap[M], lift: LiftM$arity[${`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new CatsLiftM$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowPriorityBind
        -    }
        -}
        -
        -trait LiftM${arity}Reexport{
        -  implicit def mkLM${arity}[${`Obj..N`}, Fn](implicit lift: CatsLiftM${arity}[${`Obj..N`}, Fn]): CatsLiftM${arity}.Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -}
      """
    }
  }
  object GenScalazLiftMInstances extends Template {
    override def filename(root: File): File = root / "autolift" / "scalaz" / "LiftersGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")

      val altSynTypes = (0 until arity) map (n => s"AA$n")

      val `AA..N` = altSynTypes.mkString(", ")

      val `AA >: A..N` = (altSynTypes zip synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")

      val synMTypes = synTypes.map(t => s"M[$t]")
      val synMVals = synVals.map(t => s"m$t")

      val `M[A]..M[N]` = synMTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((synMVals zip synMTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")

      def liftMApply(inner: String) = {
        val (all, last) = (synVals zip synMVals zip synTypes).splitAt(synMVals.size - 1)

        val innerBind = {
          val ((sv, smv), st) = last.head
          s"bind.map($smv) { $sv: $st => $inner }"
        }

        all.foldRight(innerBind) { case (((sv, smv), st), res) =>
          s"bind.bind($smv) { $sv: $st => $res }"
        }
      }

      val apply = liftMApply(s"f(${`a..n`})")
      val lowPriorityBind = liftMApply(s"lift(${`a..n`}, f)")


      block"""
        |package autolift.scalaz
        |
        |import scalaz.Bind
        |
        -import autolift.LiftM$arity
        -
        -trait ScalazLiftM$arity[${`Obj..N`}, Fn] extends LiftM$arity[${`Obj..N`}, Fn]
        -
        -object ScalazLiftM$arity extends LowPriorityScalazLiftM$arity {
        -  def apply[${`Obj..N`}, Fn](implicit lift: ScalazLiftM$arity[${`Obj..N`}, Fn]): Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${`A..N`}, ${`AA >: A..N`}, C](implicit bind: Bind[M]): Aux[${`M[A]..M[N]`}, (${`AA..N`}) => C, M[C]] =
        -    new ScalazLiftM$arity[${`M[A]..M[N]`}, (${`AA..N`}) => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: (${`AA..N`}) => C) = $apply
        -    }
        -}
        -
        -trait LowPriorityScalazLiftM$arity {
        -  type Aux[${`Obj..N`}, Fn, Out0] = ScalazLiftM$arity[${`Obj..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${`A..N`}, Fn](implicit bind: Bind[M], lift: LiftM$arity[${`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new ScalazLiftM$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowPriorityBind
        -    }
        -}
        -
        -trait LiftM${arity}Reexport{
        -  implicit def mkLM${arity}[${`Obj..N`}, Fn](implicit lift: ScalazLiftM${arity}[${`Obj..N`}, Fn]): ScalazLiftM${arity}.Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -}
      """
    }
  }

  object GenAlgebirdLiftMInstances extends Template {
    override def filename(root: File): File = root / "autolift" / "algebird" / "LiftersGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")

      val altSynTypes = (0 until arity) map (n => s"AA$n")

      val `AA..N` = altSynTypes.mkString(", ")

      val `AA >: A..N` = (altSynTypes zip synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")

      val synMTypes = synTypes.map(t => s"M[$t]")
      val synMVals = synVals.map(t => s"m$t")

      val `M[A]..M[N]` = synMTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((synMVals zip synMTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")

      def liftMApply(inner: String) = {
        val (all, last) = (synVals zip synMVals zip synTypes).splitAt(synMVals.size - 1)

        val innerBind = {
          val ((sv, smv), st) = last.head
          s"fm.map($smv) { $sv: $st => $inner }"
        }

        all.foldRight(innerBind) { case (((sv, smv), st), res) =>
          s"fm.flatMap($smv) { $sv: $st => $res }"
        }
      }

      val apply = liftMApply(s"f(${`a..n`})")
      val lowPriorityBind = liftMApply(s"lift(${`a..n`}, f)")


      block"""
        |package autolift.algebird
        |
        |import com.twitter.algebird.Monad
        |
        -import autolift.LiftM$arity
        -
        -trait AlgeLiftM$arity[${`Obj..N`}, Fn] extends LiftM$arity[${`Obj..N`}, Fn]
        -
        -object AlgeLiftM$arity extends LowPriorityAlgeLiftM$arity {
        -  def apply[${`Obj..N`}, Fn](implicit lift: AlgeLiftM$arity[${`Obj..N`}, Fn]): Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${`A..N`}, ${`AA >: A..N`}, C](implicit fm: Monad[M]): Aux[${`M[A]..M[N]`}, (${`AA..N`}) => C, M[C]] =
        -    new AlgeLiftM$arity[${`M[A]..M[N]`}, (${`AA..N`}) => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: (${`AA..N`}) => C) = $apply
        -    }
        -}
        -
        -trait LowPriorityAlgeLiftM$arity {
        -  type Aux[${`Obj..N`}, Fn, Out0] = AlgeLiftM$arity[${`Obj..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${`A..N`}, Fn](implicit fm: Monad[M], lift: LiftM$arity[${`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new AlgeLiftM$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowPriorityBind
        -    }
        -}
        -
        -trait LiftM${arity}Reexport{
        -  implicit def mkLM${arity}[${`Obj..N`}, Fn](implicit lift: AlgeLiftM${arity}[${`Obj..N`}, Fn]): AlgeLiftM${arity}.Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -}
      """
    }
  }

  object GenCatsLiftAInstances extends Template {
    override def filename(root: File): File = root / "autolift" / "cats" / "LiftAGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")

      val altSynTypes = (0 until arity) map (n => s"AA$n")

      val `AA..N` = altSynTypes.mkString(", ")

      val `AA >: A..N` = (altSynTypes zip synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")

      val synMTypes = synTypes.map(t => s"M[$t]")
      val synMVals = synVals.map(t => s"m$t")

      val `M[A]..M[N]` = synMTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((synMVals zip synMTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")

      def liftAApply(inner: String) = {
        val (init, last) = (synMVals.init, synMVals.last)

        val innerMap = s"ap.map($last) { $inner }"

        init.foldRight(innerMap) { case (smv, res) =>
          s"ap.ap($res) { $smv }"
        }
      }

      def fargs(inner: String) = (synVals zip synTypes).foldLeft(inner){ case (res, (sv, smv)) =>
        s"$sv: $smv => $res"
      }

      val apply = liftAApply(fargs(s"f(${`a..n`})"))
      val lowPriorityAp = liftAApply(fargs(s"lift(${`a..n`}, f)"))

      block"""
         |package autolift.cats
         |
         |import cats.Apply
         |
        -import autolift.LiftA$arity
        -
        -trait CatsLiftA$arity[${`Obj..N`}, Fn] extends LiftA$arity[${`Obj..N`}, Fn]
        -
        -object CatsLiftA$arity extends LowPriorityCatsLiftA$arity {
        -  def apply[${`Obj..N`}, Fn](implicit lift: CatsLiftA$arity[${`Obj..N`}, Fn]): Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${`A..N`}, ${`AA >: A..N`}, C](implicit ap: Apply[M]): Aux[${`M[A]..M[N]`}, (${`AA..N`}) => C, M[C]] =
        -    new CatsLiftA$arity[${`M[A]..M[N]`}, (${`AA..N`}) => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: (${`AA..N`}) => C) = $apply
        -    }
        -}
        -
        -trait LowPriorityCatsLiftA$arity {
        -  type Aux[${`Obj..N`}, Fn, Out0] = CatsLiftA$arity[${`Obj..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${`A..N`}, Fn](implicit ap: Apply[M], lift: LiftA$arity[${`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new CatsLiftA$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowPriorityAp
        -    }
        -}
        -
        -trait LiftA${arity}Reexport{
        -  implicit def mkLA${arity}[${`Obj..N`}, Fn](implicit lift: CatsLiftA${arity}[${`Obj..N`}, Fn]): CatsLiftA${arity}.Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -}
      """
    }
  }

  object GenScalazLiftAInstances extends Template {
    override def filename(root: File): File = root / "autolift" / "scalaz" / "LiftAGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")

      val altSynTypes = (0 until arity) map (n => s"AA$n")

      val `AA..N` = altSynTypes.mkString(", ")

      val `AA >: A..N` = (altSynTypes zip synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")

      val synMTypes = synTypes.map(t => s"M[$t]")
      val synMVals = synVals.map(t => s"m$t")

      val `M[A]..M[N]` = synMTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((synMVals zip synMTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")

      def liftAApply(inner: String) = {
        val (init, last) = (synMVals.init, synMVals.last)

        val innerMap = s"ap.map($last) { $inner }"

        init.foldRight(innerMap) { case (smv, res) =>
          s"ap.ap($smv) { $res }"
        }
      }

      def fargs(inner: String) = (synVals zip synTypes).foldLeft(inner){ case (res, (sv, smv)) =>
        s"$sv: $smv => $res"
      }

      val apply = liftAApply(fargs(s"f(${`a..n`})"))
      val lowPriorityAp = liftAApply(fargs(s"lift(${`a..n`}, f)"))

      block"""
        |package autolift.scalaz
        |
        |import scalaz.Apply
        |
        -import autolift.LiftA$arity
        -
        -trait ScalazLiftA$arity[${`Obj..N`}, Fn] extends LiftA$arity[${`Obj..N`}, Fn]
        -
        -object ScalazLiftA$arity extends LowPriorityScalazLiftA$arity {
        -  def apply[${`Obj..N`}, Fn](implicit lift: ScalazLiftA$arity[${`Obj..N`}, Fn]): Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${`A..N`}, ${`AA >: A..N`}, C](implicit ap: Apply[M]): Aux[${`M[A]..M[N]`}, (${`AA..N`}) => C, M[C]] =
        -    new ScalazLiftA$arity[${`M[A]..M[N]`}, (${`AA..N`}) => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: (${`AA..N`}) => C) = $apply
        -    }
        -}
        -
        -trait LowPriorityScalazLiftA$arity {
        -  type Aux[${`Obj..N`}, Fn, Out0] = ScalazLiftA$arity[${`Obj..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${`A..N`}, Fn](implicit ap: Apply[M], lift: LiftA$arity[${`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new ScalazLiftA$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowPriorityAp
        -    }
        -}
        -
        -trait LiftA${arity}Reexport{
        -  implicit def mkLA${arity}[${`Obj..N`}, Fn](implicit lift: ScalazLiftA${arity}[${`Obj..N`}, Fn]): ScalazLiftA${arity}.Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -}
      """
    }
  }

  object GenAlgebirdLiftAInstances extends Template{
    override def filename(root: File): File = root / "autolift" / "algebird" / "LiftAGen.scala"

    /* Need to account for arity + 1 DFunction. */
    override def range = 2 to (maxExportedArity - 1)

    override def content(tv: TemplateVals): String = {
      import tv._

      val `Obj..N` = ((0 until arity) map (n => s"Obj$n")).mkString(", ")
      val altSynTypes = (0 until arity) map (n => s"AA$n")
      val `AA..N` = altSynTypes.mkString(", ")
      val `AA >: A..N` = (altSynTypes zip synTypes).map(t => s"${t._1} >: ${t._2}").mkString(", ")
      val synMTypes = synTypes.map(t => s"M[$t]")
      val synMVals = synVals.map(t => s"m$t")

      val `M[A]..M[N]` = synMTypes.mkString(", ")
      val `ma: M[A]..mn: M[N]` = ((synMVals zip synMTypes) map { case (v, t) => s"$v: $t"}).mkString(", ")

      val caseStmt ={
        def casedef(cur: List[String]): String = cur match{
          case v1 :: v2 :: Nil => s"($v1, $v2)"
          case h :: t => s"($h, ${casedef(t)})"
        }
        s"case ${casedef(synVals.toList)} =>"
      }

      val joinStmt = {
        def joindef(cur: List[String], acc: String = ""): String = cur match{
          case v1 :: v2 :: Nil => s"$acc ap.join($v1, $v2)"
          case h :: t => joindef(t, s"$acc ap.join($h,")
        }
        s"""${joindef(synMVals.toList)}${")"*(arity - 2)}"""
      }

      val apply = s"ap.map(${joinStmt}){ ${caseStmt} f(${`a..n`}) }"
      val lowPriorityAp = s"ap.map(${joinStmt}){ ${caseStmt} lift(${`a..n`}, f) }" 

      block"""
        |package autolift.algebird
        |
        |import com.twitter.algebird.Applicative
        |
        -import autolift.LiftA$arity
        -
        -trait AlgeLiftA$arity[${`Obj..N`}, Fn] extends LiftA$arity[${`Obj..N`}, Fn]
        -
        -object AlgeLiftA$arity extends LowPriorityAlgeLiftA$arity {
        -  def apply[${`Obj..N`}, Fn](implicit lift: AlgeLiftA$arity[${`Obj..N`}, Fn]): Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -
        -  implicit def base[M[_], ${`A..N`}, ${`AA >: A..N`}, C](implicit ap: Applicative[M]): Aux[${`M[A]..M[N]`}, (${`AA..N`}) => C, M[C]] =
        -    new AlgeLiftA$arity[${`M[A]..M[N]`}, (${`AA..N`}) => C] {
        -      type Out = M[C]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: (${`AA..N`}) => C) = $apply
        -    }
        -}
        -
        -trait LowPriorityAlgeLiftA$arity {
        -  type Aux[${`Obj..N`}, Fn, Out0] = AlgeLiftA$arity[${`Obj..N`}, Fn] {type Out = Out0}
        -
        -  implicit def recur[M[_], ${`A..N`}, Fn](implicit ap: Applicative[M], lift: LiftA$arity[${`A..N`}, Fn]): Aux[${`M[A]..M[N]`}, Fn, M[lift.Out]] =
        -    new AlgeLiftA$arity[${`M[A]..M[N]`}, Fn] {
        -      type Out = M[lift.Out]
        -
        -      def apply(${`ma: M[A]..mn: M[N]`}, f: Fn) = $lowPriorityAp
        -    }
        -}
        -
        -trait LiftA${arity}Reexport{
        -  implicit def mkLA${arity}[${`Obj..N`}, Fn](implicit lift: AlgeLiftA${arity}[${`Obj..N`}, Fn]): AlgeLiftA${arity}.Aux[${`Obj..N`}, Fn, lift.Out] = lift
        -}
      """
    }
  }
}
