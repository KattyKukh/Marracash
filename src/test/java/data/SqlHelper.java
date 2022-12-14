package data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlHelper {
    private static QueryRunner runner = new QueryRunner();
    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");

    public SqlHelper() {
    }

    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection(url, user, password);
    }

    @SneakyThrows
    public static PaymentEntityTable getLastTransactionFromPaymentEntity() {
        var requestSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, requestSQL,
                new BeanHandler<>(PaymentEntityTable.class));
        return result;
    }

    @SneakyThrows
    public static CreditRequestEntityTable getLastTransactionFromCreditRequestEntity() {
        var requestSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, requestSQL,
                new BeanHandler<>(CreditRequestEntityTable.class));
        return result;
    }

    @SneakyThrows
    public static OrderEntityTable getLastTransactionFromOrderEntity() {
        var requestSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, requestSQL,
                new BeanHandler<>(OrderEntityTable.class));
        return result;
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var conn = getConn();
        runner.execute(conn, "DELETE FROM credit_request_entity");
        runner.execute(conn, "DELETE FROM order_entity");
        runner.execute(conn, "DELETE FROM payment_entity");
    }
}
