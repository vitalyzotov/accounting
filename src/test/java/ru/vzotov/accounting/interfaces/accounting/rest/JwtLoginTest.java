package ru.vzotov.accounting.interfaces.accounting.rest;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class JwtLoginTest {

    private static final Logger log = LoggerFactory.getLogger(JwtLoginTest.class);

    private static final String ME = "vzotov";
    private static final String PASSWORD = "1password*";
    private static String accessToken;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        if (accessToken == null) {
            accessToken = given().contentType(JSON).body("""
                            {
                               "username" : "%s",
                               "password" : "%s"
                            }
                            """.formatted(ME, PASSWORD))

                    .when().post("/auth/login?jwt=true")

                    .then().assertThat()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(JSON)
                    .log().body(true)
                    .body("accessToken", Matchers.notNullValue())
                    .body("refreshToken", Matchers.notNullValue())
                    .extract().body().jsonPath().get("accessToken");
            log.info("Access token: {}", accessToken);
        }
    }

    @Test
    public void whenUserListCards() {
        given().contentType(JSON)
                .auth().oauth2(accessToken)
                .when().get("/accounting/cards")
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .log().body(true)
                .assertThat()
                .body("size()", greaterThanOrEqualTo(1));
    }
}
