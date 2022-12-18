package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.APIHelper;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

public class DataBaseTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    void tearDown() {
        SQLHelper.cleanDatabase();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should correctly added pay in DB if approved card")
    void successAddedPayInDBIfApprovedCard() {
        var approvedCardInfo = DataHelper.generateValidApprovedCard();
        APIHelper.newPayOperation(approvedCardInfo);
        //        проверяем по статусу карты, что операция отразилась в таблице payment_entity
        String statusCard = DataHelper.getStatusCard(approvedCardInfo.getNumber());
        var rowPaymentTable = SQLHelper.getLastTransactionFromPaymentEntity();
        var rowOrderTable = SQLHelper.getLastTransactionFromOrderEntity();
        Assertions.assertEquals(statusCard, rowPaymentTable.getStatus());
        //        проверяем по id операции, что она отразилась и в таблице order_entity в столбце payment_id
        Assertions.assertEquals(rowPaymentTable.getTransaction_id(), rowOrderTable.getPayment_id());
    }

    @Test
    @DisplayName("Should correctly added Credit in DB if approved card")
    void successAddedCreditInDBIfApprovedCard() {
        var approvedCardInfo = DataHelper.generateValidApprovedCard();
        APIHelper.newCreditOperation(approvedCardInfo);
        //        проверяем по статусу карты, что операция отразилась в таблице payment_entity
        String statusCard = DataHelper.getStatusCard(approvedCardInfo.getNumber());
        var rowCreditTable = SQLHelper.getLastTransactionFromCreditRequestEntity();
        var rowOrderTable = SQLHelper.getLastTransactionFromOrderEntity();
        Assertions.assertEquals(statusCard, rowCreditTable.getStatus());
        //        проверяем по id операции, что она отразилась и в таблице order_entity в столбце credit_id
        Assertions.assertEquals(rowCreditTable.getBank_id(), rowOrderTable.getCredit_id());
    }

    @Test
    @DisplayName("Should correctly added pay in DB if declined card")
    void successAddedPayInDBIfDeclinedCard() {
        var declinedCardInfo = DataHelper.generateDeclinedCard();
        APIHelper.newPayOperation(declinedCardInfo);
        //        проверяем по статусу карты, что операция отразилась в таблице payment_entity
        String statusCard = DataHelper.getStatusCard(declinedCardInfo.getNumber());
        var rowPaymentTable = SQLHelper.getLastTransactionFromPaymentEntity();
        var rowOrderTable = SQLHelper.getLastTransactionFromOrderEntity();
        Assertions.assertEquals(statusCard, rowPaymentTable.getStatus());
        //        проверяем по id операции, что она отразилась и в таблице order_entity в столбце payment_id
        Assertions.assertEquals(rowPaymentTable.getTransaction_id(), rowOrderTable.getPayment_id());
    }

    @Test
    @DisplayName("Should correctly added Credit in DB if declined card")
    void successAddedCreditInDBIfDeclinedCard() {
        var declinedCardInfo = DataHelper.generateDeclinedCard();
        APIHelper.newCreditOperation(declinedCardInfo);
        //        проверяем по статусу карты, что операция отразилась в таблице payment_entity
        String statusCard = DataHelper.getStatusCard(declinedCardInfo.getNumber());
        var rowCreditTable = SQLHelper.getLastTransactionFromCreditRequestEntity();
        var rowOrderTable = SQLHelper.getLastTransactionFromOrderEntity();
        Assertions.assertEquals(statusCard, rowCreditTable.getStatus());
        //        проверяем по id операции, что она отразилась и в таблице order_entity в столбце credit_id
        Assertions.assertEquals(rowCreditTable.getBank_id(), rowOrderTable.getCredit_id());
    }
 }
