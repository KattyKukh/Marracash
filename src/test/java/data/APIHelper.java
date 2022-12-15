package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class APIHelper {

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void newPayOperation(DataHelper.CardInfo cardInfo) {
        given()
                .spec(requestSpec)
                .body(cardInfo)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200);
    }

    public static void newCreditOperation(DataHelper.CardInfo cardInfo) {
        given()
                .spec(requestSpec)
                .body(cardInfo)
                .when()
                .post("/api/v1/credit")
                .then()
                .statusCode(200);
    }
}
