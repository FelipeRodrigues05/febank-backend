package com.spring.bank;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires running infrastructure (MySQL, RabbitMQ, Keycloak)")
class BankApplicationTests {

    @Test
    void contextLoads() {
    }

}
