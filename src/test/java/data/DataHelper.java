package data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static Faker faker = new Faker(new Locale("en"));

    private DataHelper() {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        String number;
        String month;
        String year;
        String holder;
        String cvc;
    }

    public static String getStatusCard(String cardNumber) {
        if (cardNumber.equals("4444 4444 4444 4441")) {
            return "APPROVED";
        } else if (cardNumber.equals("4444 4444 4444 4442")) {
            return "DECLINED";
        } else {
            return null;
        }
    }

    public static CardInfo generateValidApprovedCard() {
        // задаем срок действия карты в зависимости от текущей даты, но не больше, чем +5 лет.
        var month = new DecimalFormat("00").format((int) (Math.random() * 12 + 1));
        var year = LocalDate.now().plusYears((int) (Math.random() * 4 + 1)).format(DateTimeFormatter.ofPattern("yy"));
        // генерируем имя держателя карты (не длиннее 21 символа (включая пробел))
        var holderName = generateHolderName();
        // генерируем CVC код от 001 до 999
        var cvv = new DecimalFormat("000").format(Math.random() * 999 + 1);
        return new CardInfo("4444 4444 4444 4441", month, year, holderName, cvv);
    }

    public static CardInfo generateDeclinedCard() {
        // задаем срок действия карты в зависимости от текущей даты, но не больше, чем +5 лет.
        var month = new DecimalFormat("00").format((int) (Math.random() * 12 + 1));
        var year = LocalDate.now().plusYears((int) (Math.random() * 4 + 1)).format(DateTimeFormatter.ofPattern("yy"));
        // генерируем имя держателя карты (не длиннее 21 символа (включая пробел))
        var holderName = generateHolderName();
        // генерируем CVC код от 001 до 999
        var cvv = new DecimalFormat("000").format(Math.random() * 999 + 1);
        return new CardInfo("4444 4444 4444 4442", month, year, holderName, cvv);
    }

    public static String generateHolderName() {
        // генерируем имя держателя карты (не длиннее 21 символа (включая пробел))
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName().replaceAll("[^A-Za-z]", "");
        var holderName = firstName + " " + lastName;
        // если имя и фамилия владельца оказываются длиннее 21 символа, включая пробел, то имя обрезается до одной буквы
        if (holderName.length() > 21) {
            holderName = firstName.charAt(0) + " " + lastName;
        }
        return holderName.toUpperCase();
    }

    public static String generateWrongCardNumber() {
        // генерируем номер карты с индекса "1111" для того, чтобы легче вычистить такие записи из БД, если они туда попадут
        var newCardNumber = "1111" + faker.numerify(" #### #### ####");
        return newCardNumber;
    }

    public static String generateWrongHolderName() {
        // генерируем имя держателя карты на кириллице (не длиннее 21 символа (включая пробел))
        Faker faker = new Faker(new Locale("ru"));
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName().replaceAll("[^А-Яа-я]", "");
        var holderName = firstName + " " + lastName;
        // если имя и фамилия владельца оказываются длиннее 21 символа, включая пробел, то имя обрезается до одной буквы
        if (holderName.length() > 21) {
            holderName = firstName.charAt(0) + " " + lastName;
        }
        return holderName.toUpperCase();
    }

    public static String generateLongHolderName() {
        // генерируем имя держателя карты (длиннее 21 символа (включая пробел))
        var holderName = faker.letterify("?????????? ?????????? ??????????");
        return holderName.toUpperCase();
    }
}
