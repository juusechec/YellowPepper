package com.yellowpepper.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-h2-memory.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WhenCustomerRetrievesAnAccountTest {
  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  void WithExistentAccountShouldReceiveARightResponse() throws Exception {
    ObjectNode account = getAccount(1);

    ObjectNode result =
        testRestTemplate.postForObject(
            "http://localhost:8080/v1/customers/1/retrieve-account", account, ObjectNode.class);

    assertEquals("OK", result.get("status").asText());
    assertTrue(result.get("errors").isArray());
    assertEquals(0, result.withArray("errors").size());
    assertEquals(1000, result.get("account_balance").asDouble());
  }

  @Test
  void WithNonexistentAccountShouldReceiveAKoResponse() throws Exception {
    ObjectNode account = getAccount(100);

    ObjectNode result =
        testRestTemplate.postForObject(
            "http://localhost:8080/v1/customers/1/retrieve-account", account, ObjectNode.class);

    assertEquals("KO", result.get("status").asText());
    assertTrue(result.get("errors").isArray());
    assertEquals(1, result.withArray("errors").size());
    assertEquals(
        "Account with id 100 not found in the database", result.get("errors").get(0).asText());
    assertEquals(0, result.get("account_balance").asDouble());
  }

  ObjectNode getAccount(int accountId) {
    ObjectNode newObjectInstance = mapper.createObjectNode();
    newObjectInstance.put("account", String.valueOf(accountId));
    return newObjectInstance;
  }
}
