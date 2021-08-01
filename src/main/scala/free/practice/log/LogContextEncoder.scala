package free.practice.log

abstract class LogContextEncoder[A] {
  def apply(a: A): Map[String, String]
}

object LogContextEncoder {
  @inline final def apply[A](k: String): LogContextEncoder[A] =
    (a: A) => Map(k -> a.toString)
}
