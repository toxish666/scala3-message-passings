package free.practice

import io.prometheus.client.hotspot.DefaultExports
import cats.syntax.all._
import cats.effect.{IO, Sync}

trait Wire {
  protected lazy val conf: Configuration = Configuration()

  def runInner[F[_]: Sync]: F[Unit] =
    for {
      _ <- registerPrometeusDefaultExports[F]
      _ = println("WW")
    } yield ()

  private def registerPrometeusDefaultExports[F[_]: Sync]: F[Unit] =
    Sync[F].delay(DefaultExports.initialize())
}
