package org.hyakkala.weatherapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.hyakkala.weatherapp.dao.ExternalDataDao
import org.hyakkala.weatherapp.domain.WeatherMap
import org.hyakkala.weatherapp.rest.dto.SearchProperties
import org.hyakkala.weatherapp.rest.dto.SearchType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import spock.lang.Specification;

import javax.inject.Inject;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ContextConfiguration()
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class FetchWeatherControllerTest extends Specification
{

  @Inject
  @LocalServerPort
  Integer serverPort;

  @Inject
  ObjectMapper mapper

  RESTClient client

  @Inject
  private ExternalDataDao mockExternalDataDao;

  void setup() throws URISyntaxException
  {
    client = new RESTClient("http://localhost:${serverPort}/", "application/json");
  }

  def "GET By WeatherMap by LAT_AND_LON throws 404 error if search properties is empty"()
  {
      given:
      mockExternalDataDao.getCurrentWeatherByLatAndLon(_,_) >> new WeatherMap(main: new WeatherMap.Main(temp: 101))

      when:
      getWeatherByCityName(new SearchProperties(searchType: SearchType.LAT_AND_LON, lat: 56.89,
              lon:57.89));

      then:
      thrown(Exception)
  }

  def getWeatherByCityNameThrowError()
  {
    return client.get([path: "/weather/search", contentType: 'application/json']).data
  }

}
