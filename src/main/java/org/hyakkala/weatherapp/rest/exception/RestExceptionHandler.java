package org.hyakkala.weatherapp.rest.exception;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyakkala.weatherapp.exception.ForbiddenException;
import org.hyakkala.weatherapp.exception.InputValidationException;
import org.hyakkala.weatherapp.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@ControllerAdvice
public class RestExceptionHandler
{
    private final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseMessage handleResourceNotFoundException(ResourceNotFoundException e)
    {
        log.error("The requested resource was not found", e);
        return createResponseMessage(HttpStatus.NOT_FOUND, e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InputValidationException.class)
    public ResponseMessage handleInputValidationException(InputValidationException e)
    {
        log.error("The input data is invalid", e);
        return createResponseMessage(HttpStatus.BAD_REQUEST, e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseMessage handleHttpMessageNotReadableException(final HttpMessageNotReadableException e)
    {
        log.error("HTTP message not readable", e);
        return createResponseMessage(HttpStatus.BAD_REQUEST, new InputValidationException("Malformed data"));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseMessage handleForbiddenException(ForbiddenException e)
    {
        log.error("The access to requested resource is forbidden", e);
        return createResponseMessage(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ResponseMessage> handleHttpClientErrorException(HttpClientErrorException e)
    {
        log.error("HttpClientErrorException thrown by the RestTemplate call.", e);
        ResponseMessage responseMessage = createResponseMessage(e);
        return new ResponseEntity<>(responseMessage, new HttpHeaders(), HttpStatus.valueOf(responseMessage.getCode()));
    }

    private ResponseMessage createResponseMessage(HttpStatus status, Exception e)
    {
        return new ResponseMessage(status.value(), e.getMessage());
    }

    private ResponseMessage createResponseMessage(HttpClientErrorException e)
    {
        ResponseMessage responseMessage;
        try
        {
            responseMessage = mapper.readValue(e.getResponseBodyAsString(), ResponseMessage.class);
        }
        catch (IOException e1)
        {
            log.error("Unable to parse the response body retrieved from the HttpClientErrorException.", e1);

            responseMessage = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Invalid response body " + "received from the service call.");
        }
        return responseMessage;
    }
}
