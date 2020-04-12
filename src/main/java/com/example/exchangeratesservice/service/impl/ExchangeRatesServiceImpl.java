package com.example.exchangeratesservice.service.impl;

import com.example.exchangeratesservice.exceptions.ExternalApiCallFailedException;
import com.example.exchangeratesservice.exceptions.InvalidInputException;
import com.example.exchangeratesservice.model.ExchangeRates;
import com.example.exchangeratesservice.model.ExchangeRatesResponse;
import com.example.exchangeratesservice.repository.entity.ExchangeRateEntity;
import com.example.exchangeratesservice.repository.ExchangeRatesRepository;
import com.example.exchangeratesservice.service.ExchangeRatesService;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private static final int DAYS = 30;
    public static final String RATE_DISPLAY_FORMAT = "#.##";

    @NonNull
    private RestTemplate restTemplate;

    @NonNull
    private ExchangeRatesRepository exchangeRatesRepository;

    private final String FETCH_EXCHANGE_RATES_URL = "https://api.ratesapi.io/api/%s?base=%s&symbols=%s";

    @Override
    public boolean fetchAndSaveHistoricExchangeRates(final String baseCurrency, final String exchangeRatesRequestedCurrencies, final int forLastNMonths) {
        log.info("fetchExchangeRatesAndSaveToDb called...");
        List<ExchangeRates> exchangeRates = this.fetchExchangeRatesForLastNMonths(baseCurrency, exchangeRatesRequestedCurrencies, forLastNMonths);
        List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();
        for (ExchangeRates rates : exchangeRates) {
            exchangeRateEntities.addAll(rates.getCurrencyRatesMap().keySet().stream()
                .map(
                    curr -> this.saveExchangeRates(
                        curr, rates.getCurrencyRatesMap().get(curr), rates.getBaseCurrency(), rates.getMonthCounter()
                    )
                ).collect(Collectors.toList()));
        }
        exchangeRatesRepository.saveAll(exchangeRateEntities);
        return true;
    }

    @Override
    public ExchangeRatesResponse retrieveExchangeRates() {

        log.info("retrieveExchangeRates called...");
        ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse();
        List<ExchangeRateEntity> exchangeRateEntities = exchangeRatesRepository.findAllByOrderByMonthCounterAsc();
        if (exchangeRateEntities != null && exchangeRateEntities.size() > 0) {
            Map<String, List<String>> currencyRatesMap = new HashMap<>();
            DecimalFormat decimalFormat = new DecimalFormat(RATE_DISPLAY_FORMAT);
            for(ExchangeRateEntity exchangeRateEntity: exchangeRateEntities) {
                String currency = exchangeRateEntity.getCurrency();
                String formatterRateValue = decimalFormat.format(exchangeRateEntity.getRate());
                if (currencyRatesMap.containsKey(currency)) {
                    currencyRatesMap.get(currency).add(formatterRateValue);
                } else {
                    List<String> rates = new ArrayList<>();
                    rates.add(formatterRateValue);
                    currencyRatesMap.put(currency, rates);
                }
            }
            exchangeRatesResponse.setBaseCurrency(exchangeRateEntities.get(0).getBaseCurrency());
            exchangeRatesResponse.setCurrencyRatesMap(currencyRatesMap);
        }

        return exchangeRatesResponse;
    }

    private ExchangeRateEntity saveExchangeRates(final String key, final Float rate, final String baseCurrency, final int monthCounter) {
        ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();
        exchangeRateEntity.setBaseCurrency(baseCurrency);
        exchangeRateEntity.setCurrency(key);
        exchangeRateEntity.setRate(rate);
        exchangeRateEntity.setMonthCounter(monthCounter);
        exchangeRatesRepository.save(exchangeRateEntity);
        log.info("exchange rates saved to DB");
        return exchangeRateEntity;
    }


    private List<ExchangeRates> fetchExchangeRatesForLastNMonths(final String baseCurrency, final String currencies, final int forLastNMonths) {

        List<ExchangeRates> exchangeRates = new ArrayList<>(forLastNMonths);
        Date date = new Date();
        for (int counter = 1; counter <= forLastNMonths; counter++) {
            if (counter != 1) {
                date = get30DaysPriorDate(date);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("Y-M-d");
            String strDate= formatter.format(date);
            URI uri = URI.create(String.format(FETCH_EXCHANGE_RATES_URL, strDate, baseCurrency, currencies));

            log.info("Fetching change rate from external api for date {}, for baseCurrency {} and for currencies {}", strDate, baseCurrency, currencies);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<ExchangeRates> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, ExchangeRates.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() ) {
                ExchangeRates rates = responseEntity.getBody();
                rates.setMonthCounter(counter);
                exchangeRates.add(rates);
            } else if (responseEntity.getStatusCode().is4xxClientError()){
                log.error("Inputs not accepted by external api");
                throw new InvalidInputException("Inputs not accepted by external api");
            } else if (responseEntity.getStatusCode().is5xxServerError()) {
                log.error("Error 500 returned by external API");
                throw new ExternalApiCallFailedException("Error 500 returned by external API");
            }
        }

        return exchangeRates;
    }

    private Date get30DaysPriorDate(final Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(localDate.minusDays(DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
