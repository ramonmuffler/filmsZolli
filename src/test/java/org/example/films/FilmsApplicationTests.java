package org.example.films;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.seed.enabled=false",
        "spring.data.mongodb.auto-index-creation=false"
})
class FilmsApplicationTests {

    @Test
    void contextLoads() {
    }

}
