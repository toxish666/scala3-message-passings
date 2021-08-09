import BoxConv._
import EqImps._

import scala.collection.mutable

object Main {

}

case class Box(value: DenominatorType) extends AnyVal


trait BoxConv[T] {
  type ConvRes = List[Box]

  def boxConvertible(id: T): ConvRes
}

trait BoxConvTup[P1, P2] {
  type ConvRes = List[Box]

  def boxConvertible(id: (P1, P2)): ConvRes
}

trait SomeCollection[T] {
  def add(t: T): Unit

  def contains(t: T): Boolean
}

case class SomeCollectionImpl[T: BoxConv](private val innerSet: mutable.Set[Box]) extends SomeCollection[T] {
  def add(t: T): Unit = {
    implicitly[BoxConv[T]].boxConvertible(t).foreach {
      v => innerSet.add(v)
    }
  }

  def contains(t: T): Boolean = ???
  /*implicitly[BoxConv[T]].boxConvertible(t)
    .forall(box => innerSet.exists(innerSetBox => DenominatorType.compareTypes(
      innerSetBox.value,
      box.value
    ))) */
}


object BoxConvTup {
  type Aux[P1, P2, Res] = BoxConvTup[P1, P2] {type ConvRes = Res}
}

object BoxConv {
  type Aux[T, Res] = BoxConv[T] {type ConvRes = Res}

  implicit def boxOneConvertible[K](implicit keq: Eq[K]): Aux[K, List[Box]] = new BoxConv[K] {
    def boxConvertible(id: K): ConvRes = List(
      Box(new DenominatorType {
        type InnerType = K
        val holdEqValue: K = id
        val eqInner: Eq[K] = keq
      }))
  }

  implicit def boxRecConvertible[
    T,
    E1,
    E2
  ](implicit
    ev: T <:< Tuple2[E1, E2],
    t2: BoxConvTup[E1, E2]
   ): Aux[(E1, E2), List[Box]] =
    new BoxConv[(E1, E2)] {
      def boxConvertible(id: (E1, E2)): List[Box] = t2.boxConvertible(id)
    }

  implicit def tupleBoxRightRecConvertible[
    K1,
    T2,
    KT1,
    KT2
  ](implicit
    ev: T2 <:< Tuple2[KT1, KT2],
    k1Eq: Eq[K1],
    t2: BoxConvTup[KT1, KT2]
   ): BoxConvTup.Aux[K1, (KT1, KT2), List[Box]] = new BoxConvTup[K1, (KT1, KT2)] {
    def boxConvertible(id: (K1, (KT1, KT2))): List[Box] =
      List(
        Box(new DenominatorType {
          type InnerType = K1
          val holdEqValue: K1 = id._1
        })) :::
        t2.boxConvertible(id._2)
  }

  implicit def tupleBoxLeftRecConvertible[
    T1,
    KT1,
    KT2,
    K2
  ](implicit
    ev: T1 <:< Tuple2[KT1, KT2],
    k2Eq: Eq[K2],
    t2: BoxConvTup[KT1, KT2]
   ): BoxConvTup.Aux[(KT1, KT2), K2, List[Box]] = new BoxConvTup[(KT1, KT2), K2] {
    def boxConvertible(id: ((KT1, KT2), K2)): List[Box] =
      t2.boxConvertible(id._1) :::
        List(
          Box(new DenominatorType {
            type InnerType = K2
            val holdEqValue: K2 = id._2
          }))
  }

  implicit def tupleBoxConvertible[
    T,
    K1,
    K2
  ](implicit
    ev: T <:< Tuple2[K1, K2],
    k1Eq: Eq[K1],
    k2Eq: Eq[K2]
   ): BoxConvTup.Aux[K1, K2, List[Box]] = new BoxConvTup[K1, K2] {
    def boxConvertible(id: (K1, K2)): List[Box] =
      List[Box](
        Box(new DenominatorType {
          type InnerType = K1
          val holdEqValue: K1 = id._1
        }),
        Box(new DenominatorType {
          type InnerType = K2
          val holdEqValue: K2 = id._2
        }),
      )
  }

}


