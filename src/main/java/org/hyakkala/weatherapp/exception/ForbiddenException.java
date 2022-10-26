package org.hyakkala.weatherapp.exception;

public class ForbiddenException extends RuntimeException
{

    public ForbiddenException()
    {
        super();
    }

    public ForbiddenException(String message)
    {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause)
    {
        super(cause);
    }

}
