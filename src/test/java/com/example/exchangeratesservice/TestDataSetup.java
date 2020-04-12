package com.example.exchangeratesservice;

import com.example.exchangeratesservice.model.ExchangeRatesResponse;
import com.example.exchangeratesservice.repository.entity.ExchangeRateEntity;
import java.util.ArrayList;
import java.util.List;

public class TestDataSetup {
    public static List<ExchangeRateEntity> getExchangeRateEntities() {
        List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();
        ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();
        exchangeRateEntity.setBaseCurrency("USD");
        exchangeRateEntity.setId(1);
        exchangeRateEntity.setCurrency("INR");
        exchangeRateEntity.setRate(3.24);
        exchangeRateEntity.setMonthCounter(1);
        exchangeRateEntities.add(exchangeRateEntity);
        return exchangeRateEntities;
    }

    public static ExchangeRatesResponse getExchangeRatesResponse() {
        ExchangeRatesResponse response = new ExchangeRatesResponse();
        response.setBaseCurrency("USD");
        return response;

    }
}
