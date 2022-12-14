package test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.OrderPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.open;

public class CreditTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = true;

    }

    @AfterEach
    void cleanUp() {
        closeWindow();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    String textCardExpired = "Истёк срок действия карты";
    String textWrongDate = "Неверно указан срок действия карты";
    String textFillRequired = "Поле обязательно для заполнения";
    String textWrongFormat = "Неверный формат";

    @Test
    @DisplayName("Should success credit request if valid card")
    void successCreditIfValidCard() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        orderPage.selectCredit();
        orderPage.fillAndSendForm(validCardInfo);
        orderPage.checkSuccessNotification();
    }

    @Test
    @DisplayName("Should error message if card declined")
    void errorIfDeclinedCard() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var declinedCardInfo = DataHelper.generateDeclinedCard();
        orderPage.selectCredit();
        orderPage.fillAndSendForm(declinedCardInfo);
        orderPage.checkErrorNotification();
    }

    @Test
    @DisplayName("Should error message if card number is unregistered")
    void errorIfUnregisteredCardNumber() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String newCardNumber = DataHelper.generateWrongCardNumber();
        DataHelper.CardInfo invalidCardInfo = new DataHelper.CardInfo(newCardNumber, validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(invalidCardInfo);
        orderPage.checkErrorNotification();
    }

    @Test
    @DisplayName("Should show warning if card number is empty")
    void warningIfCardNumberEmpty() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        DataHelper.CardInfo invalidCardInfo = new DataHelper.CardInfo("", validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(invalidCardInfo);
        orderPage.checkWarningCardNumber(textFillRequired);
        orderPage.checkNoNotification();
        //  в первом из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVV();
    }

    @Test
    @DisplayName("Should success credit request if card expires")
    void successIfCardExpires() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        DataHelper.CardInfo expiresCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), currentMonth, currentYear,
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(expiresCardInfo);
        orderPage.checkSuccessNotification();
    }

    @Test
    @DisplayName("Should show warning if card has expired (year)")
    void warningIfCardHasExpiredYear() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        String changedYear = LocalDate.now().plusYears(-1).format(DateTimeFormatter.ofPattern("yy"));
        DataHelper.CardInfo expiredCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), currentMonth, changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(expiredCardInfo);
        orderPage.checkWarningYear(textCardExpired);
        orderPage.checkNoNotification();
        //  в первом из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVV();
    }

    @Test
    @DisplayName("Should show warning if card has expired (month)")
    void warningIfCardHasExpiredMonth() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedMonth = LocalDate.now().plusMonths(-1).format(DateTimeFormatter.ofPattern("MM"));
        String changedYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        // если тест проводится в январе, то мы, уменьшая месяц, должны уменьшить и год, иначе карта не будет просрочена
        if (changedMonth.equals("12")) {
            changedYear = LocalDate.now().plusYears(-1).format(DateTimeFormatter.ofPattern("yy"));
        }
        DataHelper.CardInfo expiredCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), changedMonth, changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(expiredCardInfo);
        // в случае проведения теста в январе мы не увидим предупреждение под полем месяц, а только под полем год, поэтому тут проверка разветвляется
        if (changedMonth.equals("12")) {
            orderPage.checkWarningYear(textCardExpired);
            orderPage.checkNoNotification();
        } else {
            orderPage.checkWarningMonth(textWrongDate);
            orderPage.checkNoNotification();
        }
    }

    @Test
    @DisplayName("Should show warning if card has wrong month (00)")
    void warningIfCardHasWrongMonth00() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedMonth = "00";
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textWrongDate);
        orderPage.checkNoNotification();
        //  в первом из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVV();
    }

    @Test
    @DisplayName("Should show warning if card has wrong month (>12)")
    void warningIfCardHasWrongMonth() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedMonth = "13";
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textWrongDate);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show warning if card has wrong year (> +5)")
    void warningIfCardHasWrongYear() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedYear = LocalDate.now().plusYears(6).format(DateTimeFormatter.ofPattern("yy"));
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), validCardInfo.getMonth(), changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningYear(textWrongDate);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show warning if card has empty month")
    void warningIfCardHasEmptyMonth() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedMonth = "";
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textFillRequired);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show warning if holder name is Cyrillic")
    void warningIfHolderCyrillic() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedHolder = DataHelper.generateWrongHolderName();
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                changedHolder, validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCardHolder(textWrongFormat);
        orderPage.checkNoNotification();
        //  в первом из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCVV();
    }

    @Test
    @DisplayName("Should show warning if holder name is very long")
    void warningIfHolderNаmeLong() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedHolder = DataHelper.generateLongHolderName();
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                changedHolder, validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCardHolder(textWrongFormat);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show warning if holder name is empty")
    void warningIfHolderNаmeEmpty() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedHolder = "";
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                changedHolder, validCardInfo.getCvv());
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCardHolder(textFillRequired);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show warning if CVV is empty")
    void warningIfCvvEmpty() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        String changedCvv = "";
        DataHelper.CardInfo wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getCardNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), changedCvv);
        orderPage.selectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCVV(textFillRequired);
        orderPage.checkNoNotification();
        //  в первом из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
    }

    @Test
    @DisplayName("Should show warnings if form is not completed")
    void warningIfSendEmptyForm() {
        OrderPage orderPage = open("http://localhost:8080", OrderPage.class);
        var emptyCardInfo = new DataHelper.CardInfo("", "", "", "", "");
        orderPage.selectCredit();
        orderPage.fillAndSendForm(emptyCardInfo);
        orderPage.checkWarningCardNumber(textFillRequired);
        orderPage.checkWarningMonth(textFillRequired);
        orderPage.checkWarningYear(textFillRequired);
        orderPage.checkWarningCardHolder(textFillRequired);
        orderPage.checkWarningCVV(textFillRequired);
        orderPage.checkNoNotification();
    }
}
