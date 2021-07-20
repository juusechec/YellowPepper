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
class WhenCustomerRetrievesACustomerTest {
  private final ObjectMapper mapper = new ObjectMapper();
  private TestRestTemplate testRestTemplate = new TestRestTemplate();

  @Test
  void WithExistentCustomerShouldReceiveARightResponse() throws Exception {
    ObjectNode result =
        testRestTemplate.getForObject("http://localhost:8080/v1/customers/1", ObjectNode.class);

    assertEquals(1, result.get("id").asInt());
    assertTrue(result.get("firstName").isTextual());
    assertTrue(result.get("secondName").isTextual());
    assertTrue(result.get("surname").isTextual());
    assertTrue(result.get("secondSurname").isTextual());
  }

  @Test
  void WithNonexistentCustomerShouldReceiveAErrorResponse() throws Exception {
    ObjectNode result =
        testRestTemplate.getForObject("http://localhost:8080/v1/customers/100", ObjectNode.class);

    assertEquals("KO", result.get("status").asText());
    assertTrue(result.get("errors").isArray());
    assertEquals(1, result.withArray("errors").size());
    assertEquals(
        "User with id 100 not found in the database", result.get("errors").get(0).asText());
  }
}
