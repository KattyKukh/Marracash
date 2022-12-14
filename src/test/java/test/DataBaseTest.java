package test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SqlHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.OrderPage;

import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.open;

public class DataBaseTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = true;

    }

//    @AfterEach
//    void cleanUp() {
//        closeWindow();
//    }

    @AfterAll
    static void tearDownAll() {
//        SqlHelper.cleanDatabase();
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should correctly added pay in DB if approved card")
    void successAddedPayInDBIfApprovedCard() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        orderPage.selectPay();
        orderPage.fillAndSendForm(validCardInfo);
        //        проверяем по статусу карты, что операция отразилась в таблице payment_entity
        String statusCard = DataHelper.getStatusCard(validCardInfo.getCardNumber());
        String statusTableCard = SqlHelper.getStatusCard();
        var rowPaymentTable = SqlHelper.getLastTransactionFromPaymentEntity();
        var rowOrderTable = SqlHelper.getLastTransactionFromOrderEntity();
        Assertions.assertEquals(statusCard, rowPaymentTable.getStatus());
        //        проверяем по id операции, что карта отразилась и в таблице order_entity в столбце payment_id
        Assertions.assertEquals(rowPaymentTable.getTransaction_id(), rowOrderTable.getPayment_id());
        Assertions.assertEquals(rowPaymentTable.getCreated(), rowOrderTable.getCreated());
    }
    @Test
    @DisplayName("Should correctly added Credit in DB if approved card")
    void successAddedCreditInDBIfApprovedCard() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        orderPage.selectCredit();
        orderPage.fillAndSendForm(validCardInfo);
        //        проверяем по статусу карты, что операция отразилась в таблице payment_entity
        String statusCard = DataHelper.getStatusCard(validCardInfo.getCardNumber());
        var rowCreditTable = SqlHelper.getLastTransactionFromCreditRequestEntity();
        var rowOrderTable = SqlHelper.getLastTransactionFromOrderEntity();
        Assertions.assertEquals(statusCard, rowCreditTable.getStatus());
        //        проверяем по id операции, что карта отразилась и в таблице order_entity в столбце payment_id
        Assertions.assertEquals(rowCreditTable.getBank_id(), rowOrderTable.getCredit_id());
        Assertions.assertEquals(rowCreditTable.getCreated(), rowOrderTable.getCreated());
    }

}
