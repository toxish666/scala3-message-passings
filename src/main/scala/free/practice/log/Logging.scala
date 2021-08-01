package free.practice.log

trait Logging {
  final lazy val logger = Logger(this.getClass)
}
