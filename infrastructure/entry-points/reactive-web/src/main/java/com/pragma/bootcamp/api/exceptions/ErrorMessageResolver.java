package com.pragma.bootcamp.api.exceptions;

import com.pragma.bootcamp.model.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ErrorMessageResolver {

    private final MessageSource messageSource;

    public String resolve(ErrorCode errorCode, Locale locale) {
        return messageSource.getMessage(errorCode.getCode(), null, locale);
    }
}