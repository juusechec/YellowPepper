package com.yellowpepper.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FeatureCustomerTest {
  private final String CUSTOMER_ENDPOINT = "http://localhost:8080/v1/customers";
  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  @DisplayName(
      "Given a valid customer "
          + "When the user query de API "
          + "Then receives the user information ")
  void test1() throws Exception {
    ObjectNode result = testRestTemplate.getForObject(CUSTOMER_ENDPOINT + "/1", ObjectNode.class);

    assertEquals(1, result.get("id").asInt());
    assertTrue(result.get("first_name").isTextual());
    assertTrue(result.get("second_name").isTextual());
    assertTrue(result.get("surname").isTextual());
    assertTrue(result.get("second_surname").isTextual());
  }

  @Test
  @DisplayName(
      "Given an non existent customer "
          + "When the user query de API "
          + "Then receives a KO with 404 not found ")
  void test2() throws Exception {
    ObjectNode result = testRestTemplate.getForObject(CUSTOMER_ENDPOINT + "/100", ObjectNode.class);

    assertEquals("KO", result.get("status").asText());
    assertTrue(result.get("errors").isArray());
    assertEquals(1, result.withArray("errors").size());
    assertEquals(
        "User with id 100 not found in the database", result.get("errors").get(0).asText());
  }
}
