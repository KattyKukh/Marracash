package data;

import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlHelper {
    private static QueryRunner runner = new QueryRunner();
    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");

    public SqlHelper() {
    }
    @Value
    public static class PaymentEntityTable {
        private String id;
        private String amount;
        private String created;
        private String status;
        private String transaction_id;
    }
    @Value
    public static class CreditRequestEntityTable {
        private String id;
        private String bank_id;
        private String created;
        private String status;
    }

    @Value
    public static class OrderEntityTable {
        private String id;
        private String created;
        private String credit_id;
        private String payment_id;
    }
    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection(url, user, password);
    }

    @SneakyThrows
    public static String getStatusCard() {
        var requestSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConn()) {
            var result = runner.query(conn, requestSQL, new ScalarHandler<String>());
            return result;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static PaymentEntityTable getLastTransactionFromPaymentEntity() {
        var requestSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConn()) {
            var result = runner.query(conn, requestSQL,
                    new BeanHandler<>(PaymentEntityTable.class));
            return result;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static CreditRequestEntityTable getLastTransactionFromCreditRequestEntity() {
        var requestSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConn()) {
            var result = runner.query(conn, requestSQL,
                    new BeanHandler<>(CreditRequestEntityTable.class));
            return result;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static OrderEntityTable getLastTransactionFromOrderEntity() {
        var requestSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConn()) {
            var result = runner.query(conn, requestSQL,
                    new BeanHandler<>(OrderEntityTable.class));
            return result;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var conn = getConn();
        runner.execute(conn, "DELETE FROM credit_request_entity");
        runner.execute(conn, "DELETE FROM order_entity");
        runner.execute(conn, "DELETE FROM payment_entity");
    }


}
