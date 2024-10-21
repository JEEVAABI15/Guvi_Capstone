package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DevOpsJavaAppApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void helloEndpointTest() {
        // Test the /hello endpoint
        ResponseEntity<String> response = restTemplate.getForEntity("/hello", String.class);
        assertThat(response.getBody()).isEqualTo("Hello, DevOps World from Java!");
    }
}
