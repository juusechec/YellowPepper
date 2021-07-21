package com.yellowpepper.challenge;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class PropertyOverrideContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    if (!WireMockInitializer.WIREMOCK_SERVER.isRunning()) {
      WireMockInitializer.WIREMOCK_SERVER.start();
    }
    Integer wmPort = WireMockInitializer.WIREMOCK_SERVER.port();
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        configurableApplicationContext,
        "currency-service-endpoint=http://localhost:" + wmPort + "/currency-mock");
  }
}
