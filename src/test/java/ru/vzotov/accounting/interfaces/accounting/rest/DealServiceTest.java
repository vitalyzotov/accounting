package ru.vzotov.accounting.interfaces.accounting.rest;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.vzotov.accounting.interfaces.accounting.ApplicationSecurity;
import ru.vzotov.cashreceipt.domain.model.CheckOperationType;
import ru.vzotov.cashreceipt.domain.model.QRCodeData;
import ru.vzotov.cashreceipt.domain.model.QRCodeDateTime;
import ru.vzotov.domain.model.Money;
import ru.vzotov.fiscal.FiscalSign;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class DealServiceTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void whenRegisteringQR() {
        QRCodeData qr = new QRCodeData(
                new QRCodeDateTime(LocalDateTime.of(2021, 12, 7, 17, 44)),
                Money.rubles(1745.79d),
                "9280440301422813",
                "2732",
                new FiscalSign("0385604187"),
                CheckOperationType.INCOME
        );

        Map<String, String> data = new HashMap<>();
        data.put("qrcode", qr.toString());

        given()
                .contentType(JSON)
                .body(data).
                when()
                .post("/accounting/receipts/").
                then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("id", notNullValue())
        ;
    }
}
