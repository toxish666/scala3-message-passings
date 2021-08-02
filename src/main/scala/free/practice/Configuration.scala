package free.practice

import pureconfig._
import com.mongodb.ReadConcernLevel
import org.mongodb.scala.{
  MongoCredential,
  ReadConcern,
  ReadPreference,
  ServerAddress,
  WriteConcern
}

case class Configuration(
  mongo: Configuration.Mongo
)

object Configuration {
  type StringSecret = () => String

  case class Mongo(
    servers: ServerAddress,
    user: String,
    password: Option[StringSecret],
    database: String,
    msgLimit: Int,
    readConcern: ReadConcern,
    readPreference: ReadPreference,
    writeConcernForEventProcessing: WriteConcern
  ) {
    def credential: Option[() => MongoCredential] =
      for {
        user_ <- user
        password_ <- password
      } yield () => MongoCredential.createCredential(user_, "admin", password_().toCharArray)
  }

  object Mongo {

    //import pureconfig.generic.DerivedConfigReader.{given, *}

    case class HostAndPort(host: String, port: Int)

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

    val serverAddressReader: ConfigReader[ServerAddress] =
      ConfigReader.forProduct2[ServerAddress, String, Int]("host", "port") {
        (host, port) => new ServerAddress(
          host,
          port
        )
      }

    given mongoConfigReader: ConfigReader[Mongo] =
      ConfigReader.fromCursor { configCursor =>
        val fluentCursor = configCursor.fluent
        for {
          serversCurs <- fluentCursor.at(
            PathSegment.Key("servers")
          ).cursor
          serversResult <- serverAddressReader.from(serversCurs)

          msgLimitCurs <- fluentCursor.at(
            PathSegment.Key("msgLimit")
          ).cursor
          msgLimit <- msgLimitCurs.asInt


        } yield Mongo(
          msgLimit = msgLimit,
          database = "",
          user = "",
          password = Some(() => ""),
          servers = serversResult,
          readConcern = "",
          readPreference = "",
          writeConcernForEventProcessing = ""
        )
      }
  }

  //given configReader: ConfigReader[Configuration] = deriveReader[Configuration]

  /*def apply(c: Config): Configuration =
    ConfigSource.fromConfig(c).loadOrThrow[Configuration]

  def apply(): Configuration = apply(ConfigFactory.load().getConfig("practice"))*/
}
