package watweather.api

import cats.effect.Sync
import cats.implicits.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl
import watweather.service.ApiService


object ApiRoutes:

  val latitude = "latitude"
  val longitude = "longitude"

  def decodeCoordinateParam(coordinateValue: String, coordinateType: String): Either[String, Double] = {
    Either.catchNonFatal(coordinateValue.toDouble)
      .filterOrElse(c => {
        coordinateType.toLowerCase match {
          case "longitude" => -180 <= c && c <= 180
          case "latitude" => -90 <= c && c <= 90
          case _ => false
        }
      }, "")
      .leftMap(_ => s"Invalid value for parameter `$coordinateType`. Please provide a valid coordinate number.")
  }

  def routes[F[_]: Sync](W: ApiService[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case req @ GET -> Root / "watweather" =>
      val longitudeParam = req.uri.query.params.get(longitude)
      val latitudeParam = req.uri.query.params.get(latitude)

      (longitudeParam, latitudeParam) match {
        case (Some(longitudeStr), Some(latitudeStr)) =>
          (decodeCoordinateParam(longitudeStr, longitude), decodeCoordinateParam(latitudeStr, latitude))  match {
            case (Right(lon), Right(lat)) =>
              for {
                w <- W.get(longitude = lon, latitude = lat)
                resp <- Ok(w.asJson)
              } yield resp
            case (Left(errorMessage), _) =>
              BadRequest(errorMessage)
            case (_, Left(errorMessage)) =>
              BadRequest(errorMessage)
          }

        case (None, _) => BadRequest(s"Missing required query parameter `${longitude}`.")
        case (_, None) => BadRequest(s"Missing required query parameter `${latitude}`.")
      }
    }
