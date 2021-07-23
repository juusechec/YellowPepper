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
class FeatureTransactionsAmountsTest {
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
      "Given valid accounts the origin in USD and the destination in USD "
          + "When the user do a 1 USD transaction "
          + "Then the accounts have the right amounts ")
  void test1() throws Exception {
    validateAmount(9, 10, 0.0, 1.0);
  }

  @Test
  @DisplayName(
          "Given valid accounts the origin in CAD and the destination in USD "
                  + "When the user do a 1 USD transaction "
                  + "Then the accounts have the right amounts ")
  void test2() throws Exception {
    validateAmount(11, 12, 0.0, 1.0);
  }

  @Test
  @DisplayName(
          "Given valid accounts the origin in USD and the destination in CAD "
                  + "When the user do a 1 USD transaction "
                  + "Then the accounts have the right amounts ")
  void test3() throws Exception {
    validateAmount(13, 14, 0.0, 1.26);
  }

  @Test
  @DisplayName(
          "Given valid accounts the origin in CAD and the destination in CAD "
                  + "When the user do a 1 USD transaction "
                  + "Then the accounts have the right amounts ")
  void test4() throws Exception {
    validateAmount(15, 16, 0.0, 1.26);
  }

  void validateAmount(Integer originAccountId, Integer originDestinationId, Double expectedOriginAmount, Double expectedDestinationAmount) {
    ObjectNode transaction = getTransaction(1.0, "USD", originAccountId, originDestinationId, "USD to USD");

    ResponseEntity<ObjectNode> tx = executeTransaction(transaction);
    assertEquals(200, tx.getStatusCode().value());

    ObjectNode accountOrigin = executeGetAccount(originAccountId);
    assertEquals("OK", accountOrigin.get("status").asText());
    assertEquals(expectedOriginAmount, accountOrigin.get("account_balance").asDouble());

    ObjectNode accountDestination = executeGetAccount(originDestinationId);
    assertEquals("OK", accountDestination.get("status").asText());
    assertEquals(expectedDestinationAmount, accountDestination.get("account_balance").asDouble());
  }

  ObjectNode executeGetAccount(Integer accountId) {
    return testRestTemplate.postForObject(RETRIEVE_ENDPOINT, getAccount(accountId), ObjectNode.class);
  }

  ResponseEntity<ObjectNode> executeTransaction(ObjectNode transaction) {
    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    return testRestTemplate.exchange(TRANSACTIONS_ENDPOINT, HttpMethod.POST, entity, ObjectNode.class);
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
