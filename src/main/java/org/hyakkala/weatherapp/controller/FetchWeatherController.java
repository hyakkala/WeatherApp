package org.hyakkala.weatherapp.controller;

import org.hyakkala.weatherapp.common.utils.DiagnosticLogging;
import org.hyakkala.weatherapp.domain.WeatherMap;
import org.hyakkala.weatherapp.rest.dto.SearchProperties;
import org.hyakkala.weatherapp.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/weather")
public class FetchWeatherController
{

    private static final Logger LOG = LoggerFactory.getLogger(FetchWeatherController.class);
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/search")
    @DiagnosticLogging
    public ResponseEntity<WeatherMap> getCurrentWeather(
        @RequestBody
        final SearchProperties searchProperties)
    {
        LOG.debug("getCurrentWeather {}" + searchProperties);
        return new ResponseEntity<>(weatherService.getCurrentWeather(searchProperties), HttpStatus.OK);
    }

}