package com.yellowpepper.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yellowpepper.challenge.FundsTransferApplication;
import com.yellowpepper.challenge.PropertyOverrideContextInitializer;
import com.yellowpepper.challenge.WireMockInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(
    initializers = PropertyOverrideContextInitializer.class,
    classes = FundsTransferApplication.class)
class FeatureTransactionsTest {
  private final String SERVER_ENDPOINT = "http://localhost:8080";
  private final String TRANSACTIONS_ENDPOINT = SERVER_ENDPOINT + "/v1/transactions";
  private final String RETRIEVE_ENDPOINT = SERVER_ENDPOINT + "/v1/customers/1/retrieve-account";
  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @BeforeAll
  public static void setup() {
    WireMockInitializer.initialize();
  }

  @Test
  @DisplayName(
      "Given valid accounts and valid parameters "
          + "When user do a transaction with amount lower or equals to 100 "
          + "Then the response is OK With 0.2 Tax ")
  void test1() throws Exception {
    ObjectNode transaction = getTransaction(100.0, "USD", 3, 4, "Transferring across accounts");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(200, result.getStatusCode().value());
    assertEquals("OK", body.get("status").asText());
    assertEquals(0, body.withArray("errors").size());
    assertEquals(0.2, body.get("tax_collected").asDouble());
    assertTrue(body.get("CAD").isDouble());
  }

  @Test
  @DisplayName(
      "Given valid accounts and valid parameters "
          + "When user do a transaction with amount greater than 100 "
          + "Then the response is OK With 0.5 Tax ")
  void test2() throws Exception {
    ObjectNode transaction =
        getTransaction(
            101.0, "USD", 3, 4, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(200, result.getStatusCode().value());
    assertEquals("OK", body.get("status").asText());
    assertEquals(0, body.withArray("errors").size());
    assertEquals(0.5, body.get("tax_collected").asDouble());
    assertTrue(body.get("CAD").isDouble());
  }

  @Test
  @DisplayName(
      "Given valid accounts and valid parameters and sufficient amounts "
          + "When the user do a transaction 4 times "
          + "Then 4th time gives a limit error ")
  void test4() throws Exception {
    ObjectNode transaction =
        getTransaction(
            1.0, "USD", 5, 6, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);

    ResponseEntity<ObjectNode> tx1 =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    assertEquals(200, tx1.getStatusCode().value());

    ResponseEntity<ObjectNode> tx2 =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    assertEquals(200, tx2.getStatusCode().value());

    ResponseEntity<ObjectNode> tx3 =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    assertEquals(200, tx3.getStatusCode().value());

    ResponseEntity<ObjectNode> tx4 =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);

    ObjectNode body = tx4.getBody();

    assertEquals(412, tx4.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
    assertEquals("limit_exceeded", body.withArray("errors").get(0).asText());
    assertEquals(0.0, body.get("tax_collected").asDouble());
    assertEquals(0.0, body.get("CAD").asDouble());

    ObjectNode account5 =
        testRestTemplate.postForObject(RETRIEVE_ENDPOINT, getAccount(5), ObjectNode.class);
    assertEquals("OK", account5.get("status").asText());
    assertTrue(account5.get("errors").isArray());
    assertEquals(0, account5.withArray("errors").size());
    assertEquals(1487.7425489560003, account5.get("account_balance").asDouble());

    ObjectNode account6 =
        testRestTemplate.postForObject(RETRIEVE_ENDPOINT, getAccount(6), ObjectNode.class);
    assertEquals("OK", account6.get("status").asText());
    assertTrue(account6.get("errors").isArray());
    assertEquals(0, account6.withArray("errors").size());
    assertEquals(16.28, account6.get("account_balance").asDouble());
  }

  @Test
  @DisplayName(
      "Given an invalid (no existence) origin account "
          + "When the user do a transaction "
          + "Then receives a KO response ")
  void test5() throws Exception {
    ObjectNode transaction =
        getTransaction(
            100.0,
            "USD",
            100,
            1001,
            "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(400, result.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
  }

  @Test
  @DisplayName(
      "Given an invalid currency type "
          + "When the user do a transaction "
          + "Then receives a KO response with 400 Bad Request ")
  void test6() throws Exception {
    ObjectNode transaction =
        getTransaction(
            100.0, "COP", 5, 6, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(400, result.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
  }

  @Test
  @DisplayName(
      "Given a valid accounts without limits exceed "
          + "When the user do a transaction with a big amounts that exceed the current account amount "
          + "Then receives a KO response with 412 Precondition Failed insufficient funds ")
  void test7() throws Exception {
    ObjectNode transaction =
        getTransaction(
            10000.0,
            "USD",
            7,
            8,
            "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(412, result.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
    assertEquals("insufficient-funds", body.withArray("errors").get(0).asText());
  }

  ObjectNode getTransaction(
      Double amount,
      String currency,
      Integer originAccount,
      Integer destinationAccount,
      String description) {
    ObjectNode newObjectInstance = mapper.createObjectNode();
    newObjectInstance.put("amount", amount);
    newObjectInstance.put("currency", currency);
    newObjectInstance.put("origin_account", originAccount.toString());
    newObjectInstance.put("destination_account", destinationAccount.toString());
    newObjectInstance.put("description", description);
    return newObjectInstance;
  }

  ObjectNode getAccount(int accountId) {
    ObjectNode newObjectInstance = mapper.createObjectNode();
    newObjectInstance.put("account", String.valueOf(accountId));
    return newObjectInstance;
  }
}
