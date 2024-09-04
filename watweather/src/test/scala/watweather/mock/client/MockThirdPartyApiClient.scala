package watweather.mock.client

import cats.effect.{Async, Sync}
import watweather.client.ThirdPartyApiClient
import watweather.models.client.response.{Forecast, GridpointForecast}
import watweather.utils.JsonHelper

class MockThirdPartyApiClient[F[_]: Async] extends ThirdPartyApiClient[F] {

  override def getGridpointForecastByLongitudeAndLatitude(
      longitude: Double,
      latitude: Double
  ): F[GridpointForecast] =
    Sync[F].pure(JsonHelper.apply[GridpointForecast]("gridforecast.json"))

  override def getForecastByGridPoints(
      id: String,
      x: Int,
      y: Int
  ): F[Forecast] =
    Sync[F].pure(JsonHelper.apply[Forecast]("forecast.json"))
}

