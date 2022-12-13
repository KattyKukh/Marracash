package data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static Faker faker = new Faker(new Locale("en"));

    private DataHelper() {
    }

    @Value
    public static class CardInfo {
        String cardNumber;
        String month;
        String year;
        String holder;
        String cvv;
    }

    public static CardInfo generateValidApprovedCard() {
        // задаем срок действия карты в зависимости от текущей даты, но не больше, чем +5 лет.
        String month = new DecimalFormat("00").format((int) (Math.random() * 12 + 1));
        String year = LocalDate.now().plusYears((int) (Math.random() * 4 + 1)).format(DateTimeFormatter.ofPattern("yy"));
        // генерируем имя держателя карты (не длиннее 21 символа (включая пробел))
        String holderName = generateHolderName();
        // генерируем CVV код от 001 до 999
        String cvv = new DecimalFormat("000").format(Math.random() * 999 + 1);
        return new CardInfo("4444444444444441", month, year, holderName, cvv);
    }

    public static CardInfo generateDeclinedCard() {
        // задаем срок действия карты в зависимости от текущей даты, но не больше, чем +5 лет.
        String month = new DecimalFormat("00").format((int) (Math.random() * 12 + 1));
        String year = LocalDate.now().plusYears((int) (Math.random() * 4 + 1)).format(DateTimeFormatter.ofPattern("yy"));
        // генерируем имя держателя карты (не длиннее 21 символа (включая пробел))
        String holderName = generateHolderName();
        // генерируем CVV код от 001 до 999
        String cvv = new DecimalFormat("000").format(Math.random() * 999 + 1);
        return new CardInfo("4444444444444442", month, year, holderName, cvv);
    }

    public static String generateHolderName() {
        // генерируем имя держателя карты (не длиннее 21 символа (включая пробел))
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName().replaceAll("[^A-Za-z]", "");
        String holderName = firstName + " " + lastName;
        // если имя владельца оказывается длиннее 21 символа, включая пробел, то имя обрезается до одной буквы
        if (holderName.length() > 21) {
            holderName = firstName.substring(0, 1) + " " + lastName;
        }
        return holderName.toUpperCase();
    }

    public static String generateWrongCardNumber() {
        String newCardNumber = "4" + faker.numerify("###############");
        return newCardNumber;
    }

    public static String generateWrongHolderName() {
        // генерируем имя держателя карты на кириллице (не длиннее 21 символа (включая пробел))
        Faker faker = new Faker(new Locale("ru"));
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName().replaceAll("[^А-Яа-я]", "");
        String holderName = firstName + " " + lastName;
        // если имя владельца оказывается длиннее 21 символа, включая пробел, то имя обрезается до одной буквы
        if (holderName.length() > 21) {
            holderName = firstName.substring(0, 1) + " " + lastName;
        }
        return holderName.toUpperCase();
    }

    public static String generateLongHolderName() {
        // генерируем имя держателя карты (длиннее 21 символа (включая пробел))
        String holderName = faker.letterify("?????????? ?????????? ??????????");
        return holderName.toUpperCase();
    }
}
