package com.neotee.ecommercesystem;

import com.neotee.ecommercesystem.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfiguration.class)
class ECommerceSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
