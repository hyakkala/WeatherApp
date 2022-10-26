package org.hyakkala.weatherapp.rest.exception;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseMessage
{
    private static final String MAP_CODE = "code";
    private static final String MAP_MESSAGE = "message";

    private int code;

    private String message;

    public ResponseMessage()
    {
    }

    public ResponseMessage(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> response = new LinkedHashMap<>(2);
        response.put(MAP_CODE, code);
        response.put(MAP_MESSAGE, message);
        return response;
    }
}
