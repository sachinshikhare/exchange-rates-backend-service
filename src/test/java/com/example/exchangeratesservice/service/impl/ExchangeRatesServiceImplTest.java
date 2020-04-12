package com.example.exchangeratesservice.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.example.exchangeratesservice.TestDataSetup;
import com.example.exchangeratesservice.exceptions.InvalidInputException;
import com.example.exchangeratesservice.model.ExchangeRatesResponse;
import com.example.exchangeratesservice.repository.ExchangeRatesRepository;
import com.example.exchangeratesservice.repository.entity.ExchangeRateEntity;
import com.example.exchangeratesservice.service.ExchangeRatesService;
import com.example.exchangeratesservice.validator.Validator;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class ExchangeRatesServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @MockBean
    private ExchangeRatesRepository exchangeRatesRepository;

    @Autowired
    ExchangeRatesService exchangeRatesService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void fetchAndSaveHistoricExchangeRates() {

        when(exchangeRatesRepository.save(new ExchangeRateEntity())).thenReturn(new ExchangeRateEntity());
        String baseCurrency = "USD";
        String exchangeRatesRequestedCurrencies = "GBP,AUD,CAD,INR";
        int forLastNMonths = 6;
        boolean result = exchangeRatesService.fetchAndSaveHistoricExchangeRates(baseCurrency, exchangeRatesRequestedCurrencies, forLastNMonths);
        assertTrue(result);
    }


    @Test
    public void retrieveExchangeRates() {
        when(exchangeRatesRepository.findAllByOrderByMonthCounterAsc()).thenReturn(TestDataSetup.getExchangeRateEntities());
        ExchangeRatesResponse response = exchangeRatesService.retrieveExchangeRates();
        assertNotNull(response);
        assertEquals("USD", response.getBaseCurrency());
        assertEquals("3.24", response.getCurrencyRatesMap().get("INR").get(0));
    }
}