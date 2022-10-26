package org.hyakkala.weatherapp.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.hyakkala.weatherapp.dao.ExternalDataDao;
import org.hyakkala.weatherapp.domain.GeoByCityResponse;
import org.hyakkala.weatherapp.domain.GeoByZipResponse;
import org.hyakkala.weatherapp.domain.WeatherMap;
import org.hyakkala.weatherapp.rest.dto.SearchProperties;
import org.hyakkala.weatherapp.rest.dto.SearchType;
import org.hyakkala.weatherapp.exception.InputValidationException;
import org.hyakkala.weatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService
{
    @Autowired
    private ExternalDataDao externalDataDao;

    @Override
    public WeatherMap getCurrentWeather(SearchProperties searchProperties)
    {
        if (SearchType.LAT_AND_LON.equals(searchProperties.getSearchType()))
        {
            return getCurrentWeatherByLatAndLon(searchProperties.getLat(), searchProperties.getLon());
        }
        else if (SearchType.CITY.equals(searchProperties.getSearchType()))
        {
            return getCurrentWeatherByCity(searchProperties.getCity());
        }
        else if (SearchType.ZIP_CODE.equals(searchProperties.getSearchType()))
        {
            return getCurrentWeatherByZipCode(searchProperties.getZipCode());
        }
        else
        {
            throw new InputValidationException("Invalid input");
        }
    }

    private WeatherMap getCurrentWeatherByLatAndLon(Double lat, Double lon)
    {
        if (lat == null || lon == null)
        {
            throw new InputValidationException("Invalid input for getCurrentWeatherByLatAndLon");
        }
        return externalDataDao.getCurrentWeatherByLatAndLon(lat, lon);
    }

    private WeatherMap getCurrentWeatherByCity(String city)
    {
        if (StringUtils.isBlank(city))
        {
            throw new InputValidationException("Invalid input for getCurrentWeatherByCity");
        }
        GeoByCityResponse geoByCityResponse = externalDataDao.getGeoByCity(city);
        if (geoByCityResponse == null)
        {
            throw new InputValidationException("Invalid input for getCurrentWeatherByCity By Name");
        }
        return externalDataDao.getCurrentWeatherByLatAndLon(geoByCityResponse.getLat(), geoByCityResponse.getLon());
    }

    private WeatherMap getCurrentWeatherByZipCode(String zipCode)
    {
        if (StringUtils.isBlank(zipCode))
        {
            throw new InputValidationException("Invalid input for getCurrentWeatherByZipCode");
        }
        GeoByZipResponse geoByZipResponse = externalDataDao.getGeoByZipCode(zipCode);
        if (geoByZipResponse == null)
        {
            throw new InputValidationException("Invalid input for getCurrentWeatherByZipCode By Zip");
        }
        return externalDataDao.getCurrentWeatherByLatAndLon(geoByZipResponse.getLat(), geoByZipResponse.getLon());
    }
}
