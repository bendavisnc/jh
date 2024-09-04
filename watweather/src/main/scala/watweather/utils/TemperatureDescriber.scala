package watweather.utils

import io.circe.*
import watweather.utils.TemperatureDescriber.{TemperatureUnit, unitFromString}

object TemperatureDescriber:
  enum TemperatureUnit:
    case Celsius, Fahrenheit

  def unitFromString(s: String): TemperatureUnit = {
    s.toLowerCase match {
      case "c" => TemperatureUnit.Celsius
      case "f" => TemperatureUnit.Fahrenheit
      case _ =>
        throw new IllegalArgumentException(
          "Invalid temperature unit. Use 'Celsius' or 'Fahrenheit'."
        )
    }
  }

  def fromFile(filename: String): TemperatureDescriber = {
    val temperatureDescriptionMap =
      JsonHelper[Map[String, String]].apply(filename).map { case (k, v) =>
        k.toInt -> v
      }
    TemperatureDescriber(temperatureDescriptionMap)
  }

case class TemperatureDescriber(descriptions: Map[Int, String]):

  def describe(temperature: Int, unit: TemperatureUnit): String =
    val tempC = celsius(temperature, unit)
    val closestRange = descriptions.keys.toSeq.sorted.findLast(tempC >= _)
    closestRange match {
      case Some(range) => descriptions(range)
      case None        => throw new Exception("Incompatible temperature provided for given values.")
    }

  def describe(temperature: Int, unit: String): String =
    describe(temperature, unitFromString(unit))

  private def celsius(temperature: Int, unit: TemperatureUnit): Int =
    unit match {
      case TemperatureUnit.Celsius    => temperature
      case TemperatureUnit.Fahrenheit => toCelsius(temperature)
    }

  private def toCelsius(tempF: Int): Int = ((tempF - 32) * 5) / 9
