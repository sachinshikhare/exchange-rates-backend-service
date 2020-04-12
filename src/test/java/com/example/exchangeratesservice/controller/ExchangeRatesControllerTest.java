package com.example.exchangeratesservice.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import com.example.exchangeratesservice.ExchangeRatesServiceApplication;
import com.example.exchangeratesservice.TestDataSetup;
import com.example.exchangeratesservice.model.ExchangeRatesResponse;
import com.example.exchangeratesservice.service.ExchangeRatesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExchangeRatesServiceApplication.class)
@WebAppConfiguration
public class ExchangeRatesControllerTest{

    protected MockMvc mvc;

    @MockBean
    ExchangeRatesService exchangeRatesService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testFetchExchangeRatesFromExternalService() throws Exception {
        when(exchangeRatesService.fetchAndSaveHistoricExchangeRates(anyString(), anyString(), anyInt())).thenReturn(true);
        String uri = "/api/v1/exchange-rates";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void testGetExchangeRates() throws Exception {
        when(exchangeRatesService.retrieveExchangeRates()).thenReturn(TestDataSetup.getExchangeRatesResponse());
        String uri = "/api/v1/exchange-rates/months";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        System.out.println("resp: " + mvcResult.getResponse().getContentAsString());
        ExchangeRatesResponse response = mapFromJson(mvcResult.getResponse().getContentAsString(), ExchangeRatesResponse.class);
        assertEquals("USD", response.getBaseCurrency());
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz)
        throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
