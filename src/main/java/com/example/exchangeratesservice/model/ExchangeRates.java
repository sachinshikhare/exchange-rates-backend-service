package com.example.exchangeratesservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExchangeRates {

    @JsonProperty("base")
    private String baseCurrency;

    @JsonProperty("rates")
    private Map<String, Float> currencyRatesMap;

    private int monthCounter;

}
