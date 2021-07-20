package com.yellowpepper.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-h2-memory.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WhenCustomerDoATransactionTest {
  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  void WithRightValidParamsAndValidAmountEqualsTo100ShouldReceiveOkResponseWith0_2Tax() throws Exception {
    ObjectNode transaction =
        getTransaction(
            100.0, "USD", 3, 4, "Transferring across accounts");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(200, result.getStatusCode().value());
    assertEquals("OK", body.get("status").asText());
    assertEquals(0, body.withArray("errors").size());
    assertEquals(0.2, body.get("tax_collected").asDouble());
    assertEquals(0.0, body.get("CAD").asDouble());
  }

  @Test
  void WithRightValidParamsAndValidAmountGreatherThan100ShouldReceiveOkResponseWith0_5Tax() throws Exception {
    ObjectNode transaction =
            getTransaction(
                    101.0, "USD", 3, 4, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(200, result.getStatusCode().value());
    assertEquals("OK", body.get("status").asText());
    assertEquals(0, body.withArray("errors").size());
    assertEquals(0.5, body.get("tax_collected").asDouble());
    assertEquals(0.0, body.get("CAD").asDouble());
  }

  @Test
  void WithRightValidParamsButPrevious3SuccessTransactionsShouldReceiveKoResponseLimitsExceed() throws Exception {
    ObjectNode transaction =
            getTransaction(1.0, "USD", 5, 6, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> tx1 =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    assertEquals(200, tx1.getStatusCode().value());

    ResponseEntity<ObjectNode> tx2 =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    assertEquals(200, tx2.getStatusCode().value());

    ResponseEntity<ObjectNode> tx3 =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    assertEquals(200, tx3.getStatusCode().value());

    ResponseEntity<ObjectNode> tx4 =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);

    ObjectNode body = tx4.getBody();

    assertEquals(412, tx4.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
    assertEquals("limit_exceeded", body.withArray("errors").get(0).asText());
    assertEquals(0.0, body.get("tax_collected").asDouble());
    assertEquals(0.0, body.get("CAD").asDouble());
  }

  @Test
  void WithWrongOriginDestinationAndValidAmountShouldReceiveKoResponse() throws Exception {
    ObjectNode transaction =
        getTransaction(
            100.0,
            "USD",
            100,
            1001,
            "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(400, result.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
  }

  @Test
  void WithWrongCurrencyAndTheRestValidShouldReceiveKoResponse() throws Exception {
    ObjectNode transaction =
        getTransaction(
            100.0, "COP", 5, 6, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
        testRestTemplate.exchange(
            "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
    ObjectNode body = result.getBody();

    assertEquals(400, result.getStatusCode().value());
    assertEquals("KO", body.get("status").asText());
    assertEquals(1, body.withArray("errors").size());
  }

  @Test
  void WithAmountGreaterThanAccountAmountShouldReceiveKoResponse() throws Exception {
    ObjectNode transaction =
            getTransaction(
                    10000.0, "USD", 5, 6, "Hey dude! I am sending you the money you loaned to me lastweek.");

    HttpEntity<Object> entity = new HttpEntity<>(transaction);
    ResponseEntity<ObjectNode> result =
            testRestTemplate.exchange(
                    "http://localhost:8080/v1/transactions", HttpMethod.POST, entity, ObjectNode.class);
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
}
