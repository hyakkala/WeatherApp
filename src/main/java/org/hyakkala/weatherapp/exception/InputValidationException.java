package org.hyakkala.weatherapp.exception;

/**
 * Exception to represent invalid data
 */
public class InputValidationException extends RuntimeException
{

    public InputValidationException()
    {
        super();
    }

    public InputValidationException(String message)
    {
        super(message);
    }

    public InputValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InputValidationException(Throwable cause)
    {
        super(cause);
    }

}
