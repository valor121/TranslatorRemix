package SqlLite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        String createTableSql = "CREATE TABLE IF NOT EXISTS DeepL (id INTEGER PRIMARY KEY, Translations VARCHAR(255), Glossaries VARCHAR(255), ApiKey VARCHAR(255));";
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

    public void saveSelectedText(String selectedText) {
        String insertSql = "INSERT INTO DeepL (Translations) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, selectedText);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Selected text saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving selected text: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void viewTables() {
        try {
            // Get the metadata of the database
            DatabaseMetaData metaData = conn.getMetaData();

            // Retrieve the table information
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});

            // Display the table names and their column names as clickable hyperlinks
            JPanel panel = new JPanel(new GridLayout(0, 1));
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                panel.add(new JLabel(tableName));

                // Retrieve the column names for each table
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    JLabel columnLabel = new JLabel(columnName);
                    columnLabel.setForeground(Color.BLUE); // Set color to blue to indicate it's clickable
                    columnLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor to hand when hovering over the label

                    // Add mouse listener to handle click event
                    columnLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                // Fetch and display the content of the clicked column
                                String query = "SELECT " + columnName + " FROM " + tableName;
                                Statement statement = conn.createStatement();
                                ResultSet resultSet = statement.executeQuery(query);
                                StringBuilder content = new StringBuilder("Content of " + columnName + ":\n");
                                while (resultSet.next()) {
                                    content.append(resultSet.getString(columnName)).append("\n");
                                }
                                JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea(content.toString())), "Column Content", JOptionPane.INFORMATION_MESSAGE);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error viewing column content: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    panel.add(columnLabel);
                }
            }

            // Show the list of tables and their column names in a dialog
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(null, scrollPane, "Tables", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error viewing tables: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}