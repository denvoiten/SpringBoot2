package com.edu.ulab.app.web.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ExceptionHandlerUtils {

    private ExceptionHandlerUtils() {
    }

    public static String buildErrorMessage(Throwable t) {
        StringBuilder message =
                new StringBuilder(ExceptionUtils.getMessage(t));

        Throwable cause = t.getCause();
        if (cause != null) {
            message.append(", cause: ").append(ExceptionUtils.getMessage(cause));
        }

        return message.toString();
    }
}
