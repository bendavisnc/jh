package watweather.service

import cats.effect.Async
import cats.implicits.*
import org.http4s.*
import watweather.client.ThirdPartyApiClient
import watweather.models.client.response.GridpointForecast
import watweather.models.service.response.Report
import watweather.utils.TemperatureDescriber

/**
 * Provides weather-related API services.
 *
 * The ApiService trait defines the operations available for fetching weather data.
 * Implementations of this trait handle the retrieval of weather reports based on
 * location coordinates.
 *
 * @tparam F the effect type used in the service
 */
trait ApiService[F[_]]:
  def get(longitude: Double, latitude: Double): F[Report]

object ApiService:

  def impl[F[_]: Async](CThirdParty: ThirdPartyApiClient[F], temperatureDescriber: TemperatureDescriber): ApiService[F] = new ApiService[F]:

    def get(longitude: Double, latitude: Double): F[Report] =
      for {
        gridpointResponse <- CThirdParty.getGridpointForecastByLongitudeAndLatitude(longitude = longitude, latitude = latitude)

        GridpointForecast.Properties(officeId, gridX, gridY) = gridpointResponse.properties

        forecastResponse <- CThirdParty.getForecastByGridPoints(officeId, gridX, gridY)

        firstPeriod = forecastResponse.properties.periods.head // todo - Add logic for period based on current time.
        temperatureDescription = temperatureDescriber.describe(temperature = firstPeriod.temperature, unit = firstPeriod.temperatureUnit)
        shortForecast = firstPeriod.shortForecast
     } yield Report(temperature = temperatureDescription, shortForecast = shortForecast)

