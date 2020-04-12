/**
 * Copyright 2018. All rights reserved. All rights, titles and interest in and to this software are
 * owned by Market Logic Software AG, Berlin, Germany. Permission to use, copy, modify, distribute
 * or otherwise make this software available to any third party and for any purpose requires a
 * signed licensing agreement. Visit us at www.MarketLogicSoftware.com for commercial licensing
 * opportunities.
 */
package com.example.exchangeratesservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}
