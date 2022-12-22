package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class APIHelper {
    private static String urlApp = System.getProperty("app.url");
    private static String pathPayment = "/api/v1/pay";
    private static String pathCredit = "/api/v1/credit";

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(urlApp)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void newPayOperation(DataHelper.CardInfo cardInfo) {
        given()
                .spec(requestSpec)
                .body(cardInfo)
                .when()
                .post(pathPayment)
                .then()
                .statusCode(200);
    }

    public static void newCreditOperation(DataHelper.CardInfo cardInfo) {
        given()
                .spec(requestSpec)
                .body(cardInfo)
                .when()
                .post(pathCredit)
                .then()
                .statusCode(200);
    }
}
