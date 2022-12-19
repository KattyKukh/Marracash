[![Build status](https://ci.appveyor.com/api/projects/status/kyndpsplri8lmn5g/branch/master?svg=true)](https://ci.appveyor.com/project/EKukhotskaya/marracash/branch/master)
## Отчетные документы
[План автоматизации](./documents/Plan.md)<br>
[Отчет о проведенном тестировании](./documents/Report.md)<br>
[Отчет по итогам автоматизации](./documents/Summary.md)<br>

## Инструкция по запуску тестов

### Список необходимого программного обеспечения

1. Java (JDK 11)
2. Docker для запуска контейнеров симулятора оплаты на Node.js, СУБД MySQL и PostgreSQL.
3. IDE IntelliJ IDEA

### Алгоритм запуска тестов после клонирования репозитория

1. Запустить docker-compose.yml командой в терминале: docker-compose up -d
2. Подождать пока запустятся контейнеры.
3. Запустить приложение aqa-shop.jar командой в терминале: <br> 
java -Dspring.datasource.url=jdbc:mysql://localhost:3306/app -jar ./artifacts/aqa-shop.jar
4. Подождать пока запустится приложение
5. Запустить тесты командой в терминале: ./gradlew clean test
6. Подождать пока отработают тесты.
7. Остановить приложение нажатием клавиш Ctrl+C в терминале
8. Запустить приложение aqa-shop.jar командой в терминале: <br>
java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app -jar ./artifacts/aqa-shop.jar
9. Подождать пока запустится приложение.
10. Запустить тесты командой в терминале: <br>
./gradlew clean test -Ddb.url=jdbc:postgresql://localhost:5432/app
11. Подождать пока отработают тесты.
12. Остановить приложение нажатием клавиш Ctrl+C в терминале
13. Остановить контейнеры командой в терминале: docker-compose down
 
