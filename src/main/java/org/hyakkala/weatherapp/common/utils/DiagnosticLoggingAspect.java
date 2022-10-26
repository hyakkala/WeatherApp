package org.hyakkala.weatherapp.common.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DiagnosticLoggingAspect
{

    private static long nextInvocationId = 0;

    @Around("@annotation(org.hyakkala.weatherapp.common.utils.DiagnosticLogging)")
    public Object addDiagnosticLogging(ProceedingJoinPoint joinPoint) throws Throwable
    {
        String invocationId = buildInvocationId();
        Logger logger = getLogger(joinPoint);
        try
        {
            logInvocation(logger, joinPoint, invocationId);
            Object returnValue = joinPoint.proceed();
            logReturnValue(logger, returnValue, joinPoint, invocationId);
            return returnValue;
        }
        catch (Throwable t)
        {
            logThrowable(logger, t, joinPoint, invocationId);
            throw t;
        }
    }

    private void logInvocation(Logger logger, ProceedingJoinPoint joinPoint, String invocationId)
    {
        String method = joinPoint.getSignature().toShortString();
        if (joinPoint.getArgs().length > 0)
        {
            String arguments = getArgumentsString(joinPoint);
            logger.debug("{} - Entering {} with arguments ({})", invocationId, method, arguments);
        }
        else
        {
            logger.debug("{} - Entering {} (no parameters)", invocationId, method);
        }
    }

    private void logReturnValue(Logger logger, Object returnValue, ProceedingJoinPoint joinPoint, String invocationId)
    {
        String method = joinPoint.getSignature().toShortString();
        logger.debug("{} - Exiting {} with return value: {}", invocationId, method, returnValue);
    }

    private void logThrowable(Logger logger, Throwable t, ProceedingJoinPoint joinPoint, String invocationId)
    {
        String method = joinPoint.getSignature().toShortString();
        if (joinPoint.getArgs().length > 0)
        {
            String arguments = getArgumentsString(joinPoint);
            logger.error("{} - Exception invoking {} with arguments ({})", invocationId, method, arguments, t);
        }
        else
        {
            logger.error("{} - Exception invoking {} (no parameters)", invocationId, method, t);
        }
    }

    private String getArgumentsString(ProceedingJoinPoint joinPoint)
    {
        StringBuilder sb = new StringBuilder();
        Object[] arguments = joinPoint.getArgs();
        for (int i = 0; i < arguments.length; i++)
        {
            if (i > 0)
            {
                sb.append(", ");
            }
            sb.append(arguments[i]);
        }
        return sb.toString();
    }

    private static String buildInvocationId()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Invocation[");
        sb.append(nextInvocationId++);
        sb.append(']');
        return sb.toString();
    }

    Logger getLogger(ProceedingJoinPoint joinPoint)
    {
        return LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    }

}
