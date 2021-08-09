import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable
import BoxConv._
import EqImps._

class SomeTest extends AnyFunSuite {
  test("Basics tests") {

    val wsSimple = SomeCollectionImpl[Int](mutable.Set.empty)
    wsSimple.add(1)
    println(s"wsSimple  + ${wsSimple}")

    val wsTupSimple = SomeCollectionImpl[(Int, Int)](mutable.Set.empty)
    wsTupSimple.add((2, 3))
    println(s"wsTupSimple  + ${wsTupSimple}")

    val wsTupMR = SomeCollectionImpl[(Int, (Int, (Int, Int)))](mutable.Set.empty)
    wsTupMR.add((1, (2, (3, 4))))
    println(s"wsTupMR  + ${wsTupMR}")

    val wsTupML = SomeCollectionImpl[((((Int, Int), Int), Int))](mutable.Set.empty)
    wsTupML.add((((1, 2), 3), 4))
    println(s"wsTupML  + ${wsTupML}")

    val wsTupMixed = SomeCollectionImpl[((Int, (Int, Int)), Int)](mutable.Set.empty)
    wsTupMixed.add(((1, (2, 3)), 4))
    println(s"wsTupMixed  + ${wsTupMixed}")

    //println(wsTupMixed.contains(((1, (2, 3)), 4)))

    //assert(ws.contains((1, 3)))
  }
}