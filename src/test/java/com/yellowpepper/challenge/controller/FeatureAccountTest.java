package com.yellowpepper.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FeatureAccountTest {
  private final String CUSTOMER_ENDPOINT = "http://localhost:8080/v1/customers";
  private final String RETRIEVE_ENDPOINT = CUSTOMER_ENDPOINT + "/1/retrieve-account";

  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  @DisplayName(
      "Given an valid account "
          + "When the user query de API with right url "
          + "Then the user receives the account balance ")
  void test1() throws Exception {
    ObjectNode account = getAccount(1);

    ObjectNode result =
        testRestTemplate.postForObject(RETRIEVE_ENDPOINT, account, ObjectNode.class);

    assertEquals("OK", result.get("status").asText());
    assertTrue(result.get("errors").isArray());
    assertEquals(0, result.withArray("errors").size());
    assertEquals(1000, result.get("account_balance").asDouble());
  }

  @Test
  @DisplayName(
      "Given an non existent account "
          + "When the user query de API with right url "
          + "Then the user receives a KO with error not found ")
  void test2() throws Exception {
    ObjectNode account = getAccount(100);

    ObjectNode result =
        testRestTemplate.postForObject(RETRIEVE_ENDPOINT, account, ObjectNode.class);

    assertEquals("KO", result.get("status").asText());
    assertTrue(result.get("errors").isArray());
    assertEquals(1, result.withArray("errors").size());
    assertEquals(
        "Account with id 100 not found in the database", result.get("errors").get(0).asText());
    assertEquals(0, result.get("account_balance").asDouble());
  }

  @Test
  @DisplayName(
      "Given a valid customer with 2 accounts "
          + "When the user query de API "
          + "Then receives the 2 accounts information ")
  void test3() throws Exception {
    ResponseEntity<ObjectNode[]> result =
        testRestTemplate.exchange(
            CUSTOMER_ENDPOINT + "/1/accounts", HttpMethod.GET, null, ObjectNode[].class);
    ObjectNode[] body = result.getBody();

    assertEquals(200, result.getStatusCode().value());
    assertEquals(2, body.length);
  }

  @Test
  @DisplayName(
      "Given a valid customer with 2 accounts "
          + "When the user query de API with account id "
          + "Then receives the account information ")
  void test4() throws Exception {
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(
            CUSTOMER_ENDPOINT + "/1/accounts/1", HttpMethod.GET, null, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(200, result.getStatusCode().value());
    assertEquals(1, body.get("id").asInt());
  }

  ObjectNode getAccount(int accountId) {
    ObjectNode newObjectInstance = mapper.createObjectNode();
    newObjectInstance.put("account", String.valueOf(accountId));
    return newObjectInstance;
  }
}
