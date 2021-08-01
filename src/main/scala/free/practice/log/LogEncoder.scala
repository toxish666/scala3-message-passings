package free.practice.log

abstract class LogEncoder[A] {
  def apply(a: A): (String, Option[Throwable])
}

object LogEncoder {
  @inline final def apply[A]: LogEncoder[A] ?=> LogEncoder[A] =
    summon[LogEncoder[A]]
}
