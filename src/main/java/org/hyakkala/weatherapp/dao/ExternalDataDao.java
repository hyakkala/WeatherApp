package org.hyakkala.weatherapp.dao;

import org.hyakkala.weatherapp.domain.GeoByCityResponse;
import org.hyakkala.weatherapp.domain.GeoByZipResponse;
import org.hyakkala.weatherapp.domain.WeatherMap;

public interface ExternalDataDao
{

   WeatherMap getCurrentWeatherByLatAndLon(double lat, double lon);

   GeoByCityResponse getGeoByCity(String city);

   GeoByZipResponse getGeoByZipCode(String zipCode);
}