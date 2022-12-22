package page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class OrderPage {
    private ElementsCollection heading = $$("h3.heading");
    private SelenideElement cardNumber = $x("//span[text()='Номер карты']//following::input");
    private SelenideElement cardNumberWarning = $x("//span[text()='Номер карты']//following-sibling::span[@class='input__sub']");
    private SelenideElement month = $x("//span[text()='Месяц']//following::input");
    private SelenideElement monthWarning = $x("//span[text()='Месяц']//following-sibling::span[@class='input__sub']");
    private SelenideElement year = $x("//span[text()='Год']//following::input");
    private SelenideElement yearWarning = $x("//span[text()='Год']//following-sibling::span[@class='input__sub']");
    private SelenideElement cardHolder = $x("//span[text()='Владелец']//following::input");
    private SelenideElement cardHolderWarning = $x("//span[text()='Владелец']//following-sibling::span[@class='input__sub']");
    private SelenideElement cvv = $x("//span[text()='CVC/CVV']//following::input");
    private SelenideElement cvvWarning = $x("//span[text()='CVC/CVV']//following-sibling::span[@class='input__sub']");
    private SelenideElement buttonContinue = $x("//span[@class='button__text'][text()='Продолжить']");
    private SelenideElement msgSuccess = $(".notification_status_ok");
    private SelenideElement msgError = $(".notification_status_error");
    private SelenideElement msgSuccessCloser = $(".notification_status_ok .notification__closer .icon_name_close");
    private SelenideElement msgErrorCloser = $(".notification_status_error .notification__closer .icon_name_close");
    String titlePay = "Оплата по карте";
    String titleCredit = "Кредит по данным карты";
    String textSuccess = "Успешно Операция одобрена Банком.";
    String textError = "Ошибка Ошибка! Банк отказал в проведении операции.";


    public OrderPage() {
        cardFieldsVisible();
        checkNoWarningCardHolder();
        checkNoWarningMonth();
        checkNoWarningYear();
        checkNoWarningCardHolder();
        checkNoWarningCVC();
    }

    public void checkSelectPay() {
        heading.find(text(titlePay)).shouldBe(visible);
    }

    public void checkSelectCredit() {
        heading.find(text(titleCredit)).shouldBe(visible);
    }

    public void fillAndSendForm(DataHelper.CardInfo cardInfo) {
        cardNumber.setValue(cardInfo.getNumber());
        month.setValue(cardInfo.getMonth());
        year.setValue(cardInfo.getYear());
        cardHolder.setValue(cardInfo.getHolder());
        cvv.setValue(cardInfo.getCvc());
        buttonContinue.click();
    }

    public void cardFieldsVisible() {
        cardNumber.shouldBe(visible);
        month.shouldBe(visible);
        year.shouldBe(visible);
        cardHolder.shouldBe(visible);
        cvv.shouldBe(visible);
        buttonContinue.shouldBe(visible);
    }

    public void checkSuccessNotification() {
        msgSuccess.shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text(textSuccess));
        msgSuccessCloser.click();
        msgError.shouldNotBe(visible, Duration.ofSeconds(15));
    }

    public void checkErrorNotification() {
        msgError.shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text(textError));
        msgErrorCloser.click();
        msgSuccess.shouldNotBe(visible, Duration.ofSeconds(15));
    }

    public void checkNoNotification() {
        msgError.shouldNotBe(visible, Duration.ofSeconds(15));
        msgSuccess.shouldNotBe(visible, Duration.ofSeconds(15));
    }

    public void checkWarningCardNumber(String textWarning) {
        cardNumberWarning.shouldBe(visible)
                .shouldHave(text(textWarning));
    }

    public void checkNoWarningCardNumber() {
        cardNumberWarning.shouldNotBe(visible);
    }

    public void checkWarningMonth(String textWarning) {
        monthWarning.shouldBe(visible)
                .shouldHave(text(textWarning));
    }

    public void checkNoWarningMonth() {
        monthWarning.shouldNotBe(visible);
    }

    public void checkWarningYear(String textWarning) {
        yearWarning.shouldBe(visible)
                .shouldHave(text(textWarning));
    }

    public void checkNoWarningYear() {
        yearWarning.shouldNotBe(visible);
    }

    public void checkWarningCardHolder(String textWarning) {
        cardHolderWarning.shouldBe(visible)
                .shouldHave(text(textWarning));
    }

    public void checkNoWarningCardHolder() {
        cardHolderWarning.shouldNotBe(visible);
    }

    public void checkWarningCVC(String textWarning) {
        cvvWarning.shouldBe(visible)
                .shouldHave(text(textWarning));
    }

    public void checkNoWarningCVC() {
        cvvWarning.shouldNotBe(visible);
    }
}


