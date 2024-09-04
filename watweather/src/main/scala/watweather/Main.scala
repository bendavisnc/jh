package watweather

import cats.effect.{IO, IOApp}
import cats.effect.Async
import cats.syntax.all.*
import com.comcast.ip4s.*
import org.http4s.client.middleware.{RequestLogger, ResponseLogger}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import watweather.api.ApiRoutes
import watweather.service.ApiService
import watweather.client.ThirdPartyApiClient
import org.http4s._
import watweather.utils.TemperatureDescriber

object Main extends IOApp.Simple:

  def run[F[_]: Async]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build
      loggedClient = RequestLogger(logHeaders = true, logBody = true)(
        ResponseLogger(logHeaders = true, logBody = true)(client)
      )
      thirdPartyApiClient = ThirdPartyApiClient.impl[F](
        Uri.unsafeFromString(Config.thirdPartyApiBaseUri),
        loggedClient
      )
      temperatureDescriber = TemperatureDescriber.fromFile("temperatures.json")
      apiService = ApiService.impl[F](thirdPartyApiClient, temperatureDescriber)

      httpApp = (
        ApiRoutes.routes[F](apiService)
      ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(
            Host
              .fromString(Config.host)
              .getOrElse(throw new Exception("bad host config"))
          )
          .withPort(
            Port
              .fromString(Config.port)
              .getOrElse(throw new Exception("bad port config"))
          )
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever

  override val run = run[IO]
