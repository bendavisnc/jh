package watweather.client

import cats.effect.Async
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.client.Client
import watweather.models.client.response.{Forecast, GridpointForecast}

/**
 * Provides weather-related API client.
 *
 * Implementations of this trait handle the retrieval of weather reports based on
 * location coordinates from a third party based api.
 *
 * @tparam F the effect type used in the client
 */
trait ThirdPartyApiClient[F[_]]:
  def getGridpointForecastByLongitudeAndLatitude(longitude: Double, latitude: Double): F[GridpointForecast]
  def getForecastByGridPoints(id: String, x: Int, y: Int): F[Forecast]

object ThirdPartyApiClient:

  def apply[F[_]](implicit ev: ThirdPartyApiClient[F]): ThirdPartyApiClient[F] = ev

  def impl[F[_]: Async](baseUri: Uri, C: Client[F]): ThirdPartyApiClient[F] = new ThirdPartyApiClient[F]:

    override def getGridpointForecastByLongitudeAndLatitude(longitude: Double, latitude: Double): F[GridpointForecast] =
      C.expect[GridpointForecast](baseUri / "points" / s"$latitude,$longitude")

    override def getForecastByGridPoints(id: String, x: Int, y: Int): F[Forecast] =
      C.expect[Forecast](baseUri / "gridpoints" / id / s"$x,$y" / "forecast")

