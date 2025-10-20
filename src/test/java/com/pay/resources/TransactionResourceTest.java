package com.pay.resources;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.pay.resources.requests.MovimentRequest;
import com.pay.resources.requests.TransferRequest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
public class TransactionResourceTest {

    private static final String BASE_PATH = "/api/v1/transactions";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost"; 
        RestAssured.port = 8083;
    }

    @Test
    @Disabled
    void testTransferEndpoint() {
        TransferRequest request = new TransferRequest();
        request.setPayer(3);
        request.setPayee(4);
        request.setValue(new BigDecimal("100.50"));

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(BASE_PATH + "/transfer")
        .then()
            .statusCode(201);
    }

    @Test
    @Disabled
    void testDebitEndpoint() {
        MovimentRequest request = new MovimentRequest(BigDecimal.TEN, 3);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(BASE_PATH + "/debit")
        .then()
            .statusCode(201);
    }

    @Test
    @Disabled
    void testCreditEndpoint() {
        MovimentRequest request = new MovimentRequest(BigDecimal.TEN, 3);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(BASE_PATH + "/credit")
        .then()
            .statusCode(201);
    }
}
