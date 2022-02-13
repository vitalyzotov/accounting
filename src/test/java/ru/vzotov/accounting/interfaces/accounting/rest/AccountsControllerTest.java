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
import ru.vzotov.accounting.interfaces.accounting.rest.dto.AccountCreateRequest;
import ru.vzotov.banking.domain.model.BankId;
import ru.vzotov.banking.domain.model.OperationType;

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
public class AccountsControllerTest {

    public static final String GPB_ACCOUNT = "40817810418370123456";
    private static final String ME = "vzotov";
    private static final String ME_PERSON_ID = "0bf4afa9-763e-43eb-86c5-5d1898c345d2";
    private static final String STRANGER = "stranger";
    private static final String STRANGER_PERSON_ID = "d46dfa2f-6f1e-45ca-9f92-80160a5132ca";
    private static final String PASSWORD = "1password*";
    private static final String MAIN_ACCOUNT = "40817810108290123456";

    /**
     * @see AccountsController#listAccounts()
     */
    private static final String API_PATH_LIST_ACCOUNTS = "accounting/accounts";

    /**
     * @see AccountsController#newAccount(AccountCreateRequest)
     */
    private static final String API_PATH_CREATE_ACCOUNT = "accounting/accounts";

    /**
     * @see AccountsController#getAccount
     */
    private static final String API_PATH_GET_ACCOUNT = "accounting/accounts/{number}";

    /**
     * @see AccountsController#listHoldOperations(java.lang.String, java.time.LocalDate, java.time.LocalDate)
     */
    private static final String API_PATH_LIST_ACCOUNT_HOLDS = "accounting/accounts/{number}/holds";

    /**
     * @see AccountsController#listOperations(java.lang.String, java.time.LocalDate, java.time.LocalDate)
     */
    private static final String API_PATH_LIST_ACCOUNT_OPERATIONS = "accounting/accounts/{number}/operations";

    /**
     * @see AccountsController#createOperation(java.lang.String, ru.vzotov.accounting.interfaces.accounting.rest.dto.OperationCreateRequest)
     */
    private static final String API_PATH_CREATE_ACCOUNT_OPERATION = "accounting/accounts/{number}/operations";

    /**
     * @see AccountsController#modifyAccount(java.lang.String, ru.vzotov.accounting.interfaces.accounting.rest.dto.AccountModifyRequest)
     */
    private static final String API_PATH_MODIFY_ACCOUNT = "accounting/accounts/{number}";

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void whenUserListsAccounts() {
        given().contentType(JSON)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().get(API_PATH_LIST_ACCOUNTS)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("size()", greaterThanOrEqualTo(1))
                .body("collect{it.number}", hasItem(MAIN_ACCOUNT))
        ;
    }

    @Test
    public void whenUserGetsAccount() {
        given().contentType(JSON)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().get(API_PATH_GET_ACCOUNT, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("number", is(MAIN_ACCOUNT))
        ;
    }

    @Test
    public void whenStrangerGetsForeignAccount() {
        given().contentType(JSON)

                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when().get(API_PATH_GET_ACCOUNT, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON)
        ;
    }

    @Test
    public void whenStrangerListAccounts() {
        given().contentType(JSON)

                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when().get(API_PATH_LIST_ACCOUNTS)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("size()", is(0))
        ;
    }

    @Test
    public void whenUserCreatesAccount() {
        Map<String, Object> data = new HashMap<>();
        data.put("number", GPB_ACCOUNT);
        data.put("name", "Gazprombank");
        data.put("bankId", BankId.GAZPROMBANK.value());
        data.put("currency", "RUR");
        data.put("owner", ME_PERSON_ID);
        data.put("aliases", new String[]{"17416127"});

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().post(API_PATH_CREATE_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("accountNumber", is(GPB_ACCOUNT));

        // Test that we have read access to the newly created account
        given().contentType(JSON)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().get(API_PATH_GET_ACCOUNT, GPB_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("number", is(GPB_ACCOUNT));
    }

    @Test
    public void whenUserModifiesAccount() {
        Map<String, Object> data = new HashMap<>();
        data.put("number", MAIN_ACCOUNT);
        data.put("name", "Salary account");
        data.put("bankId", BankId.ALFABANK.value());
        data.put("currency", "RUR");
        data.put("owner", ME_PERSON_ID);
        data.put("aliases", new String[0]);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().put(API_PATH_MODIFY_ACCOUNT, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("accountNumber", is(MAIN_ACCOUNT));
    }

    @Test
    public void whenStrangerModifiesForeignAccount() {
        Map<String, Object> data = new HashMap<>();
        data.put("number", MAIN_ACCOUNT);
        data.put("name", "Salary account");
        data.put("bankId", BankId.ALFABANK.value());
        data.put("currency", "RUR");
        data.put("owner", STRANGER_PERSON_ID);
        data.put("aliases", new String[0]);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when().put(API_PATH_MODIFY_ACCOUNT, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON)
        ;
    }

    @Test
    public void whenUserListsOperations() {
        given().contentType(JSON)
                .auth().preemptive().basic(ME, PASSWORD)

                .when()
                .queryParam("from", "2018-07-01")
                .queryParam("to", "2018-07-31")
                .get(API_PATH_LIST_ACCOUNT_OPERATIONS, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void whenStrangerListsForeignOperations() {
        given().contentType(JSON)
                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when()
                .queryParam("from", "2018-07-01")
                .queryParam("to", "2018-07-31")
                .get(API_PATH_LIST_ACCOUNT_OPERATIONS, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON)
        ;
    }

    @Test
    public void whenUserCreatesOperation() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", "2017-05-01");
        data.put("authorizationDate", "2017-05-15");
        data.put("transactionReference", "TR20170515");
        data.put("operationType", String.valueOf(OperationType.WITHDRAW.symbol()));
        data.put("amount", 1234.56d);
        data.put("currency", "RUR");
        data.put("description", "TR20170515_DESCRIPTION");
        data.put("comment", "TR20170515_COMMENT");
        data.put("categoryId", null);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(ME, PASSWORD)

                .when().post(API_PATH_CREATE_ACCOUNT_OPERATION, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("account", is(MAIN_ACCOUNT));

    }

    @Test
    public void whenStrangerCreatesOperation() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", "2017-06-01");
        data.put("authorizationDate", "2017-06-15");
        data.put("transactionReference", "TR20170615");
        data.put("operationType", String.valueOf(OperationType.WITHDRAW.symbol()));
        data.put("amount", 6543.21d);
        data.put("currency", "RUR");
        data.put("description", "TR20170615_DESCRIPTION");
        data.put("comment", "TR20170615_COMMENT");
        data.put("categoryId", null);

        given().contentType(JSON).body(data)

                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when().post(API_PATH_CREATE_ACCOUNT_OPERATION, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON);

    }

    @Test
    public void whenUserListsHolds() {
        given().contentType(JSON)
                .auth().preemptive().basic(ME, PASSWORD)

                .when()
                .queryParam("from", "2018-07-01")
                .queryParam("to", "2018-07-31")
                .get(API_PATH_LIST_ACCOUNT_HOLDS, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(JSON)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void whenStrangerListsForeignHolds() {
        given().contentType(JSON)
                .auth().preemptive().basic(STRANGER, PASSWORD)

                .when()
                .queryParam("from", "2018-07-01")
                .queryParam("to", "2018-07-31")
                .get(API_PATH_LIST_ACCOUNT_HOLDS, MAIN_ACCOUNT)

                .then().assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(JSON)
        ;
    }
}
