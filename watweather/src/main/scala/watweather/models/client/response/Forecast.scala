package watweather.models.client.response

import io.circe.Decoder
import io.circe.generic.semiauto.*

case class Forecast(properties: Forecast.Properties)

object Forecast:

  implicit val periodDecoder: Decoder[Period] = deriveDecoder
  implicit val propertiesDecoder: Decoder[Properties] = deriveDecoder
  implicit val forecastDecoder: Decoder[Forecast] = deriveDecoder

  case class Period(
                     temperature: Int,
                     temperatureUnit: String,
                     shortForecast: String
                   )

  case class Properties(periods: List[Period])

