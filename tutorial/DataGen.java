//DEPS com.mysql:mysql-connector-j:8.3.0
//DEPS com.github.javafaker:javafaker:1.0.2

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

public class DataGen {

    private static final String INSERT = "insert into customers (first_name, last_name, email) values (?, ?, ?)";

    public static void main(String[] args) {
        String url = System.getenv("JDBC_URL");
        Objects.requireNonNull(url);

        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {

            Faker faker = new Faker();
            while (true) {
                try {
                    Name name = faker.name();
                    String firstName = name.firstName();
                    String lastName = name.lastName();
                    String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com";

                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, email);
                    preparedStatement.executeUpdate();

                    System.out.println("%s - Inserted record %s, %s, %s".formatted(Instant.now(), firstName, lastName, email));
                    Thread.sleep(Duration.ofSeconds(2));
                }
                catch (SQLIntegrityConstraintViolationException e) {
                  e.printStackTrace();
                  System.out.println("Duplicate key, continuing");
                }
            }
        }
        catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
