package free.practice

import com.mongodb.ReadConcernLevel
import org.mongodb.scala.{MongoCredential, ReadConcern, ReadPreference, ServerAddress, WriteConcern}
import pureconfig.*
import pureconfig.generic.derivation.ConfigReaderDerivation.Default.derived

import scala.deriving.Mirror

case class Configuration(
  mongo: Configuration.Mongo
) derives ConfigReader


  object Configuration {
  type StringSecret = () => String

  val config = ConfigSource.default.loadOrThrow[Configuration]

  case class Mongo(
                    servers: ServerAddress,
                    user: Option[String],
                    password: Option[StringSecret],
                    database: String,
                    msgLimit: Int,
                    readConcern: ReadConcern,
                    readPreference: ReadPreference,
                    writeConcernForEventProcessing: WriteConcern
                  ) derives ConfigReader {
    def credential: Option[() => MongoCredential] =
      for {
        user_ <- user
        password_ <- password
      } yield () => MongoCredential.createCredential(user_, "admin", password_().toCharArray)
  }

  object Mongo {

    case class HostAndPort(host: String, port: Int)

    given secretReader: ConfigReader[StringSecret] =
      ConfigReader.fromString[StringSecret](ConvertHelpers.catchReadError(x => () => x))

    given readConcernReader: ConfigReader[ReadConcern] =
      ConfigReader.fromString[ReadConcern](
        ConvertHelpers.catchReadError(rcStr => new ReadConcern(ReadConcernLevel.fromString(rcStr)))
      )

    given writeConcernReader: ConfigReader[WriteConcern] =
      ConfigReader.fromString[WriteConcern](
        ConvertHelpers.catchReadError(wcStr => com.mongodb.WriteConcern.valueOf(wcStr))
      )

    given raedPreferenceReader: ConfigReader[ReadPreference] =
      ConfigReader.fromString[ReadPreference](
        ConvertHelpers.catchReadError(rpStr => ReadPreference.valueOf(rpStr))
      )

    given serverAddressReader: ConfigReader[ServerAddress] =
      ConfigReader.forProduct2[ServerAddress, String, Int]("host", "port") {
        (host, port) =>
          new ServerAddress(
            host,
            port
          )
      }
  }
}

trait MyDerivedConfigReader[A] extends ConfigReader[A]

object MyDerivedConfigReader {
  import scala.compiletime.{erasedValue, summonInline}

  inline def summonAll[T <: Tuple]: List[MyDerivedConfigReader[_]] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts) => summonInline[MyDerivedConfigReader[t]] :: summonAll[ts]

  given[A](using cR: ConfigReader[A]):
    MyDerivedConfigReader[A] =
      new MyDerivedConfigReader[A] {
        def from(value: ConfigCursor): ConfigReader.Result[A] =
          cR.from(value)
      }

  given[H, T <: Tuple](
  using
    hDerived: MyDerivedConfigReader[H],
    tDerived: MyDerivedConfigReader[T]
  ): MyDerivedConfigReader[H *: T] = new MyDerivedConfigReader[H *: T] {
    def from(cur: ConfigCursor): ConfigReader.Result[H *: T] = {
      val cc = cur.asListCursor
        .map(Right.apply)
        .left
        .flatMap(failure => cur.asObjectCursor
          .map(Left.apply)
          .left
          .map(_ => failure)
        )
      cc.flatMap {
        case Right(listCur) => {
          val hv = hDerived.from(listCur.atIndexOrUndefined(0))
          val tv = tDerived.from(listCur.tailOption.get)
          ConfigReader.Result.zipWith(hv, tv)(_ *: _)
            .asInstanceOf[ConfigReader.Result[H *: T]]
        }
        case Left (objCur) => ???.asInstanceOf[ConfigReader.Result[H *: T]]
      }
    }
  }

  given [P <: Product](
  using
    m: Mirror.ProductOf[P],
    ts: MyDerivedConfigReader[m.MirroredElemTypes]
  ): MyDerivedConfigReader[P] = new MyDerivedConfigReader[P] {
    def from(cur: ConfigCursor): ConfigReader.Result[P] =
      ts.from(cur).map{
        (v: m.MirroredElemTypes) =>
          m.fromProduct(v)
      }
  }
}