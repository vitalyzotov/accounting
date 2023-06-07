package ru.vzotov.accounting.interfaces.accounting.rest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class SignupControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

//    @Autowired
//    PasswordEncoder encoder;

    @Test
    public void whenValidSignup() {
        //System.out.println("Password = " + encoder.encode("1password*"));
        Map<String, String> data = new HashMap<>();
        data.put("name", "user1");
        data.put("password", "1password*");
        data.put("firstName", "First");
        data.put("lastName", "Last");
        data.put("displayName", "Display");

        given()
                .contentType(JSON)
                .body(data).
                when()
                .post("/auth/signup").
                then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("name", equalTo(data.get("name")))
        ;
    }
}
