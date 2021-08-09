trait DenominatorType {
  type InnerType
  //type EqType = Eq[InnerType]
  val holdEqValue: InnerType
}

object DenominatorType {
  def compareTypes[
    L <: DenominatorType,
    R <: DenominatorType
  ](left: L, right: R)(implicit
                       ev: L#InnerType =:= R#InnerType = null
  ): Boolean = {
    ev != null && left == right
  }
}

trait Eq[A] {
  def eq(a1: A, a2: A): Boolean
}

object EqImps {

  implicit val eqInt: Eq[Int] = new Eq[Int] {
    def eq(a1: Int, a2: Int): Boolean =
      a1 == a2
  }

  implicit val eqStr: Eq[String] = new Eq[String] {
    def eq(a1: String, a2: String): Boolean =
      a1 == a2
  }
}