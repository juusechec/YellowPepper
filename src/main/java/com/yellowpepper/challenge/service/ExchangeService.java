package com.yellowpepper.challenge.service;

import com.yellowpepper.challenge.service.model.CurrencyServiceResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ExchangeService {
  private static final Logger LOGGER = Logger.getLogger(ExchangeService.class.getName());

  private CurrencyServiceResponse responseCache = null;

  @Value("${currency-service-endpoint}")
  private String currencyServiceEndpoint;

  private void saveCache(CurrencyServiceResponse responseCache) {
    this.responseCache = responseCache;
  }

  private CurrencyServiceResponse getCache() {
    return this.responseCache;
  }

  public Mono<Double> fromUSDtoCAD(double usd) {
    return getInfoCurrenciesFromService()
        .map(
            body -> {
              if (!body.getSuccess().booleanValue()) {
                throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CURRENCIES SERVICE FAILED CONNECTION NOT AVAILABLE TO TRANSFORM FROM USD TO CAD");
              } else {
                saveCache(body);
                double cad = usd * body.getRates().getCad();
                LOGGER.log(Level.INFO, "From USD to CAD: {0}USD", usd);
                LOGGER.log(Level.INFO, "From USD to CAD: {0}CAD", cad);
                return cad;
              }
            });
  }

  public Mono<Double> fromCADtoUSD(double cad) {
    return getInfoCurrenciesFromService()
        .map(
            body -> {
              if (!body.getSuccess().booleanValue()) {
                throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CURRENCIES SERVICE FAILED CONNECTION NOT AVAILABLE TO TRANSFORM FROM CAD TO USD");
              } else {
                saveCache(body);
                double usd = cad / body.getRates().getCad();
                LOGGER.log(Level.INFO, "From CAD to USD: {0}CAD", cad);
                LOGGER.log(Level.INFO, "From CAD to USD: {0}USD", usd);
                return usd;
              }
            });
  }

  public Mono<CurrencyServiceResponse> getInfoCurrenciesFromService() {
    if (responseCache == null) {
      WebClient.ResponseSpec responseSpec =
          getClient().get().uri(currencyServiceEndpoint).retrieve();
      return responseSpec.bodyToMono(CurrencyServiceResponse.class);
    } else {
      return Mono.just(this.getCache());
    }
  }

  public WebClient getClient() {
    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

    return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
  }
}
