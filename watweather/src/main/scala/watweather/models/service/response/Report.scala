package watweather.models.service.response

import cats.effect.Async
import io.circe.*
import io.circe.generic.semiauto.*
import org.http4s.*
import org.http4s.circe.*


case class Report(temperature: String, shortForecast: String)

object Report:
  given Decoder[Report] = Decoder.derived[Report]

  given [F[_] : Async]: EntityDecoder[F, Report] = jsonOf

  given Encoder[Report] = Encoder.AsObject.derived[Report]

  given [F[_]]: EntityEncoder[F, Report] = jsonEncoderOf

