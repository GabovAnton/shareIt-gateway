package ru.practicum.shareit.gateway;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = ShareItGateway.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {
        "SHAREIT_SERVER_URL=http://localhost:8080",
})
class ShareItGatewayTest {
    @Test
    void contextLoads() {

    }
}