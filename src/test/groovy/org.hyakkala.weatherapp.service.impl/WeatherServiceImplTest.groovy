import org.hyakkala.weatherapp.dao.ExternalDataDao
import org.hyakkala.weatherapp.domain.GeoByCityResponse
import org.hyakkala.weatherapp.domain.WeatherMap
import org.hyakkala.weatherapp.exception.InputValidationException
import org.hyakkala.weatherapp.rest.dto.SearchProperties
import org.hyakkala.weatherapp.rest.dto.SearchType
import org.hyakkala.weatherapp.service.WeatherService
import org.hyakkala.weatherapp.service.impl.WeatherServiceImpl
import spock.lang.Specification

class WeatherServiceImplTest extends Specification
{

  ExternalDataDao externalDataDao = Mock()
  WeatherService weatherService = new WeatherServiceImpl(externalDataDao: externalDataDao)

  def setup()
  {
  }

  def "weatherService should throw error if SearchProperties with Search Type is empty"()
  {
    when:
    weatherService.getCurrentWeather(new SearchProperties())

    then:
    thrown(InputValidationException)
  }

  def "weatherService should return success temp for searchType: LAT_AND_LON"()
  {
    given:

    externalDataDao.getCurrentWeatherByLatAndLon(_,_) >> new WeatherMap(main: new WeatherMap.Main(temp: 100))
    when:
    def response = weatherService.getCurrentWeather(new SearchProperties(searchType: SearchType.LAT_AND_LON, lat: 39.88,
            lon: -83.44))

    then:
    response.main.temp == 100
  }

  def "weatherService should fail where searchType: LAT_AND_LON if lat is empty or lon is empty"()
  {
    when:
    weatherService.getCurrentWeather(new SearchProperties(searchType: SearchType.LAT_AND_LON, lat: 39.88,
            lon: null))

    then:
    thrown(InputValidationException)

    when:
    weatherService.getCurrentWeather(new SearchProperties(searchType: SearchType.LAT_AND_LON, lat: 39.88,
            lon: null))

    then:
    thrown(InputValidationException)
  }

  def "weatherService should return success temp for searchType: CITY"()
  {
    given:
    externalDataDao.getGeoByCity("FAIRFAX,VA") >> new GeoByCityResponse(lat:55.88, lon:45.45)
    externalDataDao.getCurrentWeatherByLatAndLon(_,_) >>
            new WeatherMap(main: new WeatherMap.Main(temp: 101))
    when:
    def response = weatherService.getCurrentWeather(new SearchProperties(searchType: SearchType.CITY,
            city: "FAIRFAX,VA"))

    then:
    response.main.temp == 101
  }

}