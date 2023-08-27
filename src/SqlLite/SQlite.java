package SqlLite;

import java.awt.*;
import java.sql.*;

public class SQlite extends Component {

    private Connection conn;

    public SQlite(String databaseUrl) {
        try {
            conn = DriverManager.getConnection(databaseUrl);
            String databaseName = "DeepL";
            System.out.println("Connected to the database: " + databaseName);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS DeepL (id integer PRIMARY KEY, Translations varchar(50), Glossaries varchar(50), ApiKey varchar(50));";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String SetApiKey(String apiKey) {
      //set api key only once
        String insertSql = "INSERT INTO DeepL (ApiKey) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, apiKey);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertSql;
    }


    public String deleteKeyMenu() {
      //shows the keys listed and allows the user to select which key to delete

       String deleteSql = "DELETE FROM DeepL WHERE ApiKey = ?";
       //shows the keys listed and allows the user to select which key to delete
         String readSql = "SELECT ApiKey FROM DeepL";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(readSql)) {
                if (rs.next()) {
                    return rs.getString("ApiKey");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //return the key from the 1st row
            return readSql;
        }
}

