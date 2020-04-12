package com.example.exchangeratesservice.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRatesResponse {

    private String baseCurrency;

    private Map<String, List<String>> currencyRatesMap;
}
