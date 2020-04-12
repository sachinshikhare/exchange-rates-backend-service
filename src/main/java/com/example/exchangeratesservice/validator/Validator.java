package com.example.exchangeratesservice.validator;

import com.example.exchangeratesservice.exceptions.InvalidInputException;
import org.springframework.util.StringUtils;

public class Validator {
    public static void validateInputs(final String baseCurrency, final String exchangeRatesRequestedCurrencies, final int forLastNMonths) {
        if (StringUtils.isEmpty(baseCurrency)) {
            throw new InvalidInputException("Please provide valid value for baseCurrency");
        }
        if (StringUtils.isEmpty(exchangeRatesRequestedCurrencies)) {
            throw new InvalidInputException("Please provide valid value for exchangeRatesRequestedCurrencies");
        }
        if (forLastNMonths <= 0) {
            throw new InvalidInputException("forLastNMonths should non-zero positive value");
        }
    }
}
