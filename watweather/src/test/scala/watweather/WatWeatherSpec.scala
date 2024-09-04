package watweather

import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*
import watweather.api.ApiRoutes
import watweather.client.ThirdPartyApiClient
import watweather.mock.client.MockThirdPartyApiClient
import watweather.service.ApiService
import watweather.utils.TemperatureDescriber

class WatWeatherSpec extends CatsEffectSuite:

  val testLongitude = "-78.2158"
  val testLatitude = "34.0347"

  test(
    "ApiService should return `BadRequest` if required query params are missing"
  ) {
    val request = mockRequestWithQueryParams("latitude" -> testLatitude)
    assertIO(request.map(_.status), Status.BadRequest) *>
      assertIO(
        request.flatMap(_.as[String]),
        "\"Missing required query parameter `longitude`.\""
      )
  }

  test(
    "ApiService should return `BadRequest` if query params are non-numeric"
  ) {
    val request = mockRequestWithQueryParams(
      "longitude" -> "squirrel",
      "latitude" -> testLatitude
    )
    assertIO(request.map(_.status), Status.BadRequest) *>
      assertIO(
        request.flatMap(_.as[String]),
        "\"Invalid value for parameter `longitude`. Please provide a valid coordinate number.\""
      )
  }

  test(
    "ApiService should return `BadRequest` if longitude query param is out of range"
  ) {
    val request = mockRequestWithQueryParams(
      "longitude" -> "180.01",
      "latitude" -> testLatitude
    )
    assertIO(request.map(_.status), Status.BadRequest) *>
      assertIO(
        request.flatMap(_.as[String]),
        "\"Invalid value for parameter `longitude`. Please provide a valid coordinate number.\""
      )
  }

  test(
    "ApiService should return `BadRequest` if latitude query param is out of range"
  ) {
    val request = mockRequestWithQueryParams(
      "longitude" -> testLongitude,
      "latitude" -> "90.01"
    )
    assertIO(request.map(_.status), Status.BadRequest) *>
      assertIO(
        request.flatMap(_.as[String]),
        "\"Invalid value for parameter `latitude`. Please provide a valid coordinate number.\""
      )
  }

  test(
    "ApiService should return successful response if query params are valid"
  ) {
    val request = mockRequestWithQueryParams(
      "longitude" -> testLongitude,
      "latitude" -> testLatitude
    )
    assertIO(request.map(_.status), Status.Ok) *>
      assertIO(
        request.flatMap(_.as[String]),
        "{\"temperature\":\"Cool, Pleasant\",\"shortForecast\":\"Mostly Clear\"}"
      )
  }

  private val testTemperatureDescriber: TemperatureDescriber =
    TemperatureDescriber.fromFile("temperatures.json")

  private val testMockThirdPartyApiClient: ThirdPartyApiClient[IO] =
    new MockThirdPartyApiClient[IO]

  private val apiService: ApiService[IO] =
    ApiService.impl[IO](testMockThirdPartyApiClient, testTemperatureDescriber)

  private val apiRoutes: HttpApp[IO] =
    ApiRoutes.routes(apiService).orNotFound

  private def mockRequestWithQueryParams(
      params: (String, String)*
  ): IO[Response[IO]] =
    val uriWithParams = params.foldLeft(uri"/watweather")((uri, param) =>
      uri.withQueryParam(param._1, param._2)
    )
    val request = Request[IO](Method.GET, uriWithParams)
    apiRoutes.run(request)
