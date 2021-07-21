package com.yellowpepper.challenge;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockInitializer {
  static final WireMockServer WIREMOCK_SERVER = new WireMockServer(wireMockConfig().dynamicPort());

  public static void initialize() {
    if (!WireMockInitializer.WIREMOCK_SERVER.isRunning()) {
      WireMockInitializer.WIREMOCK_SERVER.start();
    }
    configureFor("localhost", WIREMOCK_SERVER.port());
    stubFor(
        get("/currency-mock")
            .willReturn(
                ok().withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\n"
                            + "    \"success\": true,\n"
                            + "    \"timestamp\": 1626582424,\n"
                            + "    \"base\": \"USD\",\n"
                            + "    \"date\": \"2021-07-18\",\n"
                            + "    \"rates\": {\n"
                            + "        \"CAD\": 1.26\n"
                            + "    }\n"
                            + "}")));
  }
}
