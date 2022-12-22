package page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {

    private SelenideElement titlePage = $("h2");
    //    кнопки ищем через button, а потом уже span, чтобы при изменении структуры страницы искать именно кнопку с надписью, а не просто надпись
    private SelenideElement buttonPay = $x("//button//span[text()='Купить']");
    private SelenideElement buttonCredit = $x("//button//span[text()='Купить в кредит']");


    public MainPage() {
        titlePage.shouldHave(text("Путешествие дня")).shouldBe(visible);
        buttonPay.shouldBe(visible);
        buttonCredit.shouldBe(visible);
    }

    public OrderPage selectPay() {
        buttonPay.click();
        return new OrderPage();
    }

    public OrderPage selectCredit() {
        buttonCredit.click();
        return new OrderPage();
    }

}


