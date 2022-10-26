package org.hyakkala.weatherapp.dao.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyakkala.weatherapp.domain.GeoByCityResponse;
import org.hyakkala.weatherapp.domain.GeoByZipResponse;
import org.hyakkala.weatherapp.exception.ForbiddenException;
import org.hyakkala.weatherapp.exception.InputValidationException;
import org.hyakkala.weatherapp.exception.ResourceNotFoundException;
import org.hyakkala.weatherapp.dao.ExternalDataDao;
import org.hyakkala.weatherapp.domain.WeatherMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalDataDaoImpl implements ExternalDataDao
{

    @Value("${openweathermap.api.url}")
    private String weatherApiUrl;

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;

    private static final Logger LOG = LoggerFactory.getLogger(ExternalDataDaoImpl.class);

    @Override
    @Cacheable(cacheNames = "WeatherMap", key = "#lat+'_'+#lon")
    public WeatherMap getCurrentWeatherByLatAndLon(double lat, double lon)
    {
        RestTemplate restTemplate = new RestTemplate();
        String currentWeatherUrl = weatherApiUrl + "data/2.5/weather?appid=" + weatherApiKey + "&exclude=minutely," +
            "hourly,daily,alerts&units=imperial&lat=" + lat + "&lon=" + lon;
        try
        {
            LOG.debug("missed cache {}", currentWeatherUrl);
            ResponseEntity<WeatherMap> response =
                restTemplate.exchange(currentWeatherUrl, HttpMethod.GET, null, WeatherMap.class);
            return response.getBody();
        }
        catch (HttpClientErrorException e)
        {
            return handleStatusCodeExceptionForPost(currentWeatherUrl, e);
        }
    }

    @Override
    @Cacheable(cacheNames = "GeoByCityResponse", key = "#city")
    public GeoByCityResponse getGeoByCity(String city)
    {
        RestTemplate restTemplate = new RestTemplate();
        //http://api.openweathermap.org/geo/1.0/direct?q={city name},{state code},{country
        // code}&limit={limit}&appid={API key}
        String geoByZipCityUrl = weatherApiUrl + "geo/1.0/direct?limit=1&appid=" + weatherApiKey + "&q=" + city;
        try
        {
            LOG.debug("missed cache {}", geoByZipCityUrl);
            ResponseEntity<Object[]> response =
                restTemplate.exchange(geoByZipCityUrl, HttpMethod.GET, null, Object[].class);
            ObjectMapper mapper = new ObjectMapper();
            Optional<GeoByCityResponse> geoByCityResponse =
                Arrays.stream(Objects.requireNonNull(response.getBody())).map(object -> mapper.convertValue(object, GeoByCityResponse.class))
                    .findFirst();
            if (geoByCityResponse.isPresent())
            {
                return geoByCityResponse.get();
            }
            else
            {
                throw new InputValidationException("Invalid input By geoByZipCity");
            }
        }
        catch (HttpClientErrorException e)
        {
            return handleStatusCodeExceptionForPost(geoByZipCityUrl, e);
        }
    }

    @Override
    @Cacheable(cacheNames = "GeoByZipResponse", key = "#zipCode")
    public GeoByZipResponse getGeoByZipCode(String zipCode)
    {
        RestTemplate restTemplate = new RestTemplate();
        //http://api.openweathermap.org/geo/1.0/zip?zip={zip code},{country code}&appid={API key}
        String geoByZipCodeUrl = weatherApiUrl + "geo/1.0/zip?appid=" + weatherApiKey + "&zip=" + zipCode;
        try
        {
            LOG.debug("missed cache {}", geoByZipCodeUrl);
            ResponseEntity<GeoByZipResponse> response =
                restTemplate.exchange(geoByZipCodeUrl, HttpMethod.GET, null, GeoByZipResponse.class);
            return response.getBody();
        }
        catch (HttpClientErrorException e)
        {
            return handleStatusCodeExceptionForPost(geoByZipCodeUrl, e);
        }
    }

    private <T> T handleStatusCodeExceptionForPost(String url, HttpClientErrorException e)
    {
        String responseText = e.getResponseBodyAsString();

        if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value())
        {
            LOG.error("HTTP Status Code 400 for URL {} ", url);
            throw new InputValidationException(responseText, e);
        }
        else if (e.getRawStatusCode() == HttpStatus.NOT_FOUND.value())
        {
            LOG.error("HTTP Status Code 404 for URL {} ", url);
            throw new ResourceNotFoundException(responseText, e);
        }
        else if (e.getRawStatusCode() == HttpStatus.FORBIDDEN.value())
        {
            LOG.error("HTTP Status Code 403 for URL {} ", url);
            throw new ForbiddenException(responseText, e);
        }
        else
        {
            LOG.error("Unknown Error: {} :: {}", url, e.getResponseBodyAsString());
            throw new IllegalStateException(responseText, e);
        }
    }
}