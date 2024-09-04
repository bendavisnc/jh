# WatWeather

WatWeather is a REST API service that provides weather forecasts based on geographical coordinates (longitude and latitude). The application fetches data from a third-party weather service and processes the information to return temperature descriptions and short weather forecasts.

## Features

- **API Service**: Returns weather reports based on latitude and longitude coordinates.
- **Mock Data Support**: Includes mock data clients for local testing.
- **Temperature Description**: Provides human-readable descriptions of temperatures based on a configurable JSON file.

## Getting Started

To build and run the server:
```bash
sbt run
```

To build and run the server with automatic reloading:
```bash
sbt ~reStart
```
To build and run tests with automatic reloading:
```bash
sbt ~test
```
See `Makefile`

### Sample Response

```json
{
  "temperature": "Chilly, Brisk",
  "shortForecast": "Showers And Thunderstorms Likely then Showers And Thunderstorms"
}
```

## Project Structure

- `Main.scala`: The main entry point for the application.
- `ApiRoutes.scala`: Defines the HTTP routes for the API.
- `ApiService.scala`: The core service logic that interacts with the weather client and the temperature describer.
- `ThirdPartyApiClient.scala`: Defines the client interface for fetching weather data from a third-party service.
- `TemperatureDescriber.scala`: Provides descriptions for temperatures based on a JSON file.
- `MockThirdPartyApiClient.scala`: A mock implementation of the `ThirdPartyApiClient` for local testing.
- `resources/`: Contains configuration files like `temperatures.json`.

