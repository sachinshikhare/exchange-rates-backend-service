package com.example.exchangeratesservice.service;

import com.example.exchangeratesservice.model.ExchangeRatesResponse;

public interface ExchangeRatesService {

    boolean fetchAndSaveHistoricExchangeRates(final String baseCurrency, final String exchangeRatesRequestedCurrencies, final int forLastNMonths);

    ExchangeRatesResponse retrieveExchangeRates();
}
