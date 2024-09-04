package watweather.utils

import io.circe.Decoder
import io.circe.parser.decode

import scala.io.Source

object JsonHelper {
  // todo - Make this `IO aware`
  def apply[A: Decoder](filename: String): A = {
    val source = Source.fromResource(filename)
    val jsonString = try source.mkString finally source.close()
    decode[A](jsonString) match {
      case Right(a) => a
      case Left(err) => throw new Exception(s"Failed to parse JSON: $err")
    }
  }
}
