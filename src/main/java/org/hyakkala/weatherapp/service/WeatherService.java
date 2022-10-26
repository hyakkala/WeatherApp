package org.hyakkala.weatherapp.service;

import org.hyakkala.weatherapp.domain.WeatherMap;
import org.hyakkala.weatherapp.rest.dto.SearchProperties;

public interface WeatherService
{
     WeatherMap getCurrentWeather(SearchProperties searchProperties);
}
