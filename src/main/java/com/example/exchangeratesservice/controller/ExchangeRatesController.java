package com.example.exchangeratesservice.controller;

import com.example.exchangeratesservice.model.ExchangeRatesResponse;
import com.example.exchangeratesservice.service.ExchangeRatesService;
import com.example.exchangeratesservice.validator.Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExchangeRatesController {

    @NonNull
    ExchangeRatesService exchangeRatesService;


    private static boolean EXCHANGE_RATES_FETCHED = false;

    @CrossOrigin
    @RequestMapping(value = "/exchange-rates",
        produces = { "application/json" },
        method = RequestMethod.GET)
    public ResponseEntity<Void> fetchExchangeRatesFromExternalService() {

        log.info("fetchExchangeRatesFromExternalService called...");

        if (EXCHANGE_RATES_FETCHED) {
            // Once exchange rate fetched and loaded to database, we dont need to fetch again.
            log.info("exchange rates already fetched and saved in db. No need to fetch again. returning success.");
            return ResponseEntity.ok().build();
        }
        String baseCurrency = "USD";
        String exchangeRatesRequestedCurrencies = "GBP,AUD,CAD,INR";
        int forLastNMonths = 6;

        Validator.validateInputs(baseCurrency, exchangeRatesRequestedCurrencies, forLastNMonths);
        boolean success = exchangeRatesService.fetchAndSaveHistoricExchangeRates(baseCurrency, exchangeRatesRequestedCurrencies, forLastNMonths);
        if (success) {
            EXCHANGE_RATES_FETCHED = true;
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @CrossOrigin
    @RequestMapping(value = "/exchange-rates/months",
        produces = { "application/json" },
        method = RequestMethod.GET)
    public ExchangeRatesResponse getExchangeRates() {

        log.info("getExchangeRates called...");
        return exchangeRatesService.retrieveExchangeRates();
    }
}
