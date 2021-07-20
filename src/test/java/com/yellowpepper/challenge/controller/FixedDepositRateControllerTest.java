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
public class FixedDepositRateControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Test
    public void getAllToDos() throws Exception {
        ObjectNode newObjectInstance = mapper.createObjectNode();
        newObjectInstance.put("account", "1");

        //when
        ObjectNode result = testRestTemplate.postForObject(
                "http://localhost:8080/v1/customers/130303/retrieve-account", newObjectInstance, ObjectNode.class);

        //then
        assertEquals("OK", result.get("status").asText());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
