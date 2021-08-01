package free.practice

import cats.effect._
import cats.implicits._
import free.practice.Main.runInner

object Main extends IOApp with Wire {
  def run(args: List[String]) = runInner[IO].as(ExitCode.Success)
}
