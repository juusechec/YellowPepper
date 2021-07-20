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

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-h2-memory.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WhenCustomerDoATransactionTest {
  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  void WithRightOriginDestinationAndValidAmountShouldReceiveOkResponse() throws Exception {
    ObjectNode transaction =
        getTransaction(
            100.0, "USD", 2, 3, "Hey dude! I am sending you the money you loaned to me lastweek.");

    ObjectNode result =
        testRestTemplate.postForObject(
            "http://localhost:8080/v1/transactions", transaction, ObjectNode.class);

    assertEquals("OK", result.get("status").asText());
    assertEquals(0, result.withArray("errors").size());
    assertEquals(0.2, result.get("tax_collected").asDouble());
    assertEquals(0.0, result.get("CAD").asDouble());
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
