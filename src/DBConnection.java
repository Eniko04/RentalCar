import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Деклариране на статична променлива за съхранение на връзката
    static Connection conn = null;

    // Метод за създаване и връщане на връзка към базата данни
    static Connection getConnection() {
        try {
            // Зареждане на драйвера за H2 база данни
            Class.forName("org.h2.Driver");

            // Установяване на връзка към локална H2 база с предоставени данни за връзка
            conn = DriverManager.getConnection(
                    "jdbc:h2:/Users/enizali/Desktop/h2/MyDataBase", "sa", "Eniz1234"
            );
        } catch (ClassNotFoundException e) {
            // Обработка на грешка, ако не е намерен драйвера
            e.printStackTrace();
        } catch (SQLException e) {
            // Обработка на грешка, ако има проблем при свързването към базата
            e.printStackTrace();
        }

        // Връщане на създадената връзка
        return conn;
    }
}
