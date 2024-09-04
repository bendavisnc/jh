package watweather.models.client.response

import io.circe.Decoder
import io.circe.generic.semiauto.*

case class GridpointForecast(properties: GridpointForecast.Properties)

object GridpointForecast:

  implicit val propertiesDecoder: Decoder[Properties] = deriveDecoder
  implicit val gridpointForecastDecoder: Decoder[GridpointForecast] = deriveDecoder

  case class Properties(gridId: String, gridX: Int, gridY: Int)

