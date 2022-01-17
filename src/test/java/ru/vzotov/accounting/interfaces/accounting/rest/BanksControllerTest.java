package ru.vzotov.accounting.interfaces.accounting.rest;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.vzotov.banking.domain.model.BankId;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class BanksControllerTest {

    private static final String ADMIN = "admin";
    private static final String ME = "vzotov";
    private static final String STRANGER = "stranger";
    private static final String PASSWORD = "1password*";

    /**
     * @see BanksController#listBanks()
     */
    private static final String API_PATH_LIST_BANKS = "/accounting/banks";

    /**
     * @see BanksController#getBank(java.lang.String)
     */
    private static final String API_PATH_GET_BANK = "/accounting/banks/{bankId}";

    /**
     * @see BanksController#createBank(ru.vzotov.accounting.interfaces.accounting.rest.dto.BankCreateRequest)
     */
    private static final String API_PATH_CREATE_BANK = "/accounting/banks";

    /**
     * @see BanksController#modifyBank(java.lang.String, ru.vzotov.accounting.interfaces.accounting.rest.dto.BankCreateRequest)
     */
    private static final String API_PATH_MODIFY_BANK = "/accounting/banks/{bankId}";


    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void whenStrangerListsBanks() {
        given().contentType(JSON)

                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when().get(API_PATH_LIST_BANKS)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("size()", greaterThanOrEqualTo(1))
                .body("collect{it.bankId}", hasItem(BankId.ALFABANK.value()))
        ;
    }

    @Test
    public void whenUserGetsBank() {
        given().contentType(JSON)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().get(API_PATH_GET_BANK, BankId.ALFABANK.value())

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("bankId", is(BankId.ALFABANK.value()))
        ;
    }

    @Test
    public void whenUserCreatesBank() {
        Map<String, Object> data = prepareCreateBankRequest(BankId.GAZPROMBANK);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().post(API_PATH_CREATE_BANK)

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON);
    }

    @Test
    public void whenAdminCreatesBank() {
        Map<String, Object> data = prepareCreateBankRequest(BankId.GAZPROMBANK);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ADMIN, PASSWORD)

                .when().post(API_PATH_CREATE_BANK)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("bankId", is(data.get("bankId")));
    }

    @Test
    public void whenUserModifiesBank() {
        Map<String, Object> data = prepareCreateBankRequest(BankId.ALFABANK);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().put(API_PATH_MODIFY_BANK, BankId.ALFABANK.value())

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON);
    }

    @Test
    public void whenAdminModifiesBank() {
        Map<String, Object> data = prepareCreateBankRequest(BankId.ALFABANK);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ADMIN, PASSWORD)

                .when().put(API_PATH_MODIFY_BANK, BankId.ALFABANK.value())

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("bankId", is(data.get("bankId")));
    }

    private Map<String, Object> prepareCreateBankRequest(BankId bankId) {
        Map<String, Object> data = new HashMap<>();
        if (BankId.GAZPROMBANK.equals(bankId)) {
            data.put("bankId", BankId.GAZPROMBANK.value());
            data.put("name", "Gazprombank");
            data.put("shortName", "GPB");
            data.put("longName", "Gazprombank long name");
        } else if (BankId.ALFABANK.equals(bankId)) {
            data.put("bankId", BankId.ALFABANK.value());
            data.put("name", "Alfa");
            data.put("shortName", "Alfa");
            data.put("longName", "Alfabank long name");
        } else throw new IllegalArgumentException();
        return data;
    }

}
