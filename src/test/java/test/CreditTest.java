package test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.MainPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.open;

public class CreditTest {
    private static String urlApp = System.getProperty("app.url");

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
        SQLHelper.cleanDatabase();
        SelenideLogger.removeListener("allure");
    }

    String textCardExpired = "Истёк срок действия карты";
    String textWrongDate = "Неверно указан срок действия карты";
    String textFillRequired = "Поле обязательно для заполнения";
    String textWrongFormat = "Неверный формат";

    @Test
    @DisplayName("Should success credit request if valid card")
    void successCreditIfValidCard() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(validCardInfo);
        orderPage.checkSuccessNotification();
    }

    @Test
    @DisplayName("Should error message if card declined")
    void errorIfDeclinedCard() {
        var mainPage = open(urlApp, MainPage.class);
        var declinedCardInfo = DataHelper.generateDeclinedCard();
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(declinedCardInfo);
        orderPage.checkErrorNotification();
    }

    @Test
    @DisplayName("Should error message if card number is unregistered")
    void errorIfUnregisteredCardNumber() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var newCardNumber = DataHelper.generateWrongCardNumber();
        var invalidCardInfo = new DataHelper.CardInfo(newCardNumber, validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(invalidCardInfo);
        orderPage.checkErrorNotification();
    }

    @Test
    @DisplayName("Should error message if card number is too short")
    void errorIfShortCardNumber() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var newCardNumber = DataHelper.generateWrongCardNumber().substring(0, 15);
        var invalidCardInfo = new DataHelper.CardInfo(newCardNumber, validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(invalidCardInfo);
        orderPage.checkWarningCardNumber(textWrongFormat);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if card number is empty")
    void warningIfCardNumberEmpty() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var invalidCardInfo = new DataHelper.CardInfo("", validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(invalidCardInfo);
        orderPage.checkWarningCardNumber(textFillRequired);
        orderPage.checkNoNotification();
        //  в одном из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVC();
    }

    @Test
    @DisplayName("Should success credit request if card expires")
    void successIfCardExpires() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        var currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        var expiresCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), currentMonth, currentYear,
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(expiresCardInfo);
        orderPage.checkSuccessNotification();
    }

    @Test
    @DisplayName("Should show correct warning if card has expired (year)")
    void warningIfCardHasExpiredYear() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        var changedYear = LocalDate.now().plusYears(-1).format(DateTimeFormatter.ofPattern("yy"));
        var expiredCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), currentMonth, changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(expiredCardInfo);
        orderPage.checkWarningYear(textCardExpired);
        orderPage.checkNoNotification();
        //  в одном из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVC();
    }

    @Test
    @DisplayName("Should show correct warning if card has expired (month)")
    void warningIfCardHasExpiredMonth() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedMonth = LocalDate.now().plusMonths(-1).format(DateTimeFormatter.ofPattern("MM"));
        var changedYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        // если тест проводится в январе, то мы, уменьшая месяц, должны уменьшить и год, иначе карта не будет просрочена
        if (changedMonth.equals("12")) {
            changedYear = LocalDate.now().plusYears(-1).format(DateTimeFormatter.ofPattern("yy"));
        }
        var expiredCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), changedMonth, changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(expiredCardInfo);
        // в случае проведения теста в январе мы не увидим предупреждение под полем месяц, а только под полем год,
        // поэтому тут проверка разветвляется
        if (changedMonth.equals("12")) {
            orderPage.checkWarningYear(textCardExpired);
            orderPage.checkNoNotification();
        } else {
            orderPage.checkWarningMonth(textWrongDate);
            orderPage.checkNoNotification();
        }
    }

    @Test
    @DisplayName("Should show correct warning if wrong month (00)")
    void warningIfCardHasWrongMonth00() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedMonth = "00";
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textWrongDate);
        orderPage.checkNoNotification();
        //  в одном из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVC();
    }

    @Test
    @DisplayName("Should show correct warning if too short month")
    void warningIfShortMonth() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedMonth = validCardInfo.getMonth().substring(1, 2);
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textWrongFormat);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if wrong month (>12)")
    void warningIfCardHasWrongMonth() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedMonth = "13";
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textWrongDate);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if empty month")
    void warningIfCardHasEmptyMonth() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedMonth = "";
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), changedMonth, validCardInfo.getYear(),
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningMonth(textFillRequired);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if wrong year (> +5)")
    void warningIfCardHasWrongYear() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedYear = LocalDate.now().plusYears(6).format(DateTimeFormatter.ofPattern("yy"));
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningYear(textWrongDate);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if too short number of year")
    void warningIfShortNumberOfYear() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedYear = validCardInfo.getYear().substring(1, 2);
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningYear(textWrongFormat);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if empty year")
    void warningIfEmptyYear() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedYear = "";
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), changedYear,
                validCardInfo.getHolder(), validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningYear(textFillRequired);
        orderPage.checkNoNotification();
    }


    @Test
    @DisplayName("Should show correct warning if holder name is Cyrillic")
    void warningIfHolderCyrillic() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedHolder = DataHelper.generateWrongHolderName();
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                changedHolder, validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCardHolder(textWrongFormat);
        orderPage.checkNoNotification();
        //  в одном из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCVC();
    }

    @Test
    @DisplayName("Should show correct warning if holder name is very long")
    void warningIfHolderNameLong() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedHolder = DataHelper.generateLongHolderName();
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                changedHolder, validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCardHolder(textWrongFormat);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if holder name is empty")
    void warningIfHolderNameEmpty() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedHolder = "";
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                changedHolder, validCardInfo.getCvc());
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCardHolder(textFillRequired);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show correct warning if CVC is too short")
    void warningIfShortCvc() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedCvc = validCardInfo.getCvc().substring(0, 2);
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), changedCvc);
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCVC(textWrongFormat);
        orderPage.checkNoNotification();
        //  в одном из тестов на валидацию поля можно проверять, не всплывает ли предупреждение под другими полями
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
    }

    @Test
    @DisplayName("Should show correct warning if CVC is empty")
    void warningIfCvcEmpty() {
        var mainPage = open(urlApp, MainPage.class);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        var changedCvc = "";
        var wrongCardInfo = new DataHelper.CardInfo(validCardInfo.getNumber(), validCardInfo.getMonth(), validCardInfo.getYear(),
                validCardInfo.getHolder(), changedCvc);
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(wrongCardInfo);
        orderPage.checkWarningCVC(textFillRequired);
        orderPage.checkNoNotification();
    }

    @Test
    @DisplayName("Should show no notification and show correct warnings if all form fields was empty")
    void warningIfSendEmptyForm() {
        var mainPage = open(urlApp, MainPage.class);
        var emptyCardInfo = new DataHelper.CardInfo("", "", "", "", "");
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(emptyCardInfo);
        orderPage.checkNoNotification();
        orderPage.checkWarningCardNumber(textFillRequired);
        orderPage.checkWarningMonth(textFillRequired);
        orderPage.checkWarningYear(textFillRequired);
        orderPage.checkWarningCardHolder(textFillRequired);
        orderPage.checkWarningCVC(textFillRequired);
    }

    @Test
    @DisplayName("Should show no warnings if form fields was empty and then form was filled with valid data")
    void showNoWarningIfEmptyFormWasFilledValidData() {
        var mainPage = open(urlApp, MainPage.class);
        var emptyCardInfo = new DataHelper.CardInfo("", "", "", "", "");
        var orderPage = mainPage.selectCredit();
        orderPage.checkSelectCredit();
        orderPage.fillAndSendForm(emptyCardInfo);
        var validCardInfo = DataHelper.generateValidApprovedCard();
        orderPage.fillAndSendForm(validCardInfo);
        orderPage.checkNoWarningCardNumber();
        orderPage.checkNoWarningMonth();
        orderPage.checkNoWarningYear();
        orderPage.checkNoWarningCardHolder();
        orderPage.checkNoWarningCVC();
    }
}
