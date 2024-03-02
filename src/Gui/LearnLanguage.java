package Gui;

import DeepLApi.DeepLApiBuilder;
import DeepLApi.Translator;

import DictionaryApi.DictionaryApi;
import DictionaryApi.DictJson;
import SqlLite.SQlite;
import org.intellij.lang.annotations.Language;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static DeepLApi.DeepLApiBuilder.*;
//Todo: rebuild the Translate.py script, to make it work with the language menu.
//It's hard coded for German to english, as of now.
//refactoring code,make it cleaner ect ect.

public class LearnLanguage extends JFrame {
    private String translationMethod;
    public String language;

    public JTextArea textArea;
    private String targetLanguageCode;


    public LearnLanguage() { //this is the main gui
        super("Learn Language");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        setLayout(new BorderLayout());

        Choose();


        LanguageMenu();
        Main_Text_Area();

        Translate_Button();
        LookUp_Button();
        addSqlMenu(); // Add Sql menu


        setVisible(true);


    }
    private void addSqlMenu() {

        JMenuBar menuBar = getJMenuBar(); // Get the existing menu bar
        JMenu sqlMenu = new JMenu("Sql");
        JMenuItem saveTextMenuItem = new JMenuItem("Save Text");
        JMenuItem ViewDB = new JMenuItem("ViewDB");
        SQlite sqlite = new SQlite("jdbc:sqlite:DeepL.db"); // Pass the database URL if needed


        saveTextMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedText = textArea.getSelectedText();
                if (selectedText != null && !selectedText.isEmpty()) {
                    saveSelectedText(selectedText);
                } else {
                    JOptionPane.showMessageDialog(null, "No text selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }


        });
        // Add ActionListener to handle viewing database tables
        ViewDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sqlite.viewTables();
            }
        });
        sqlMenu.add(ViewDB);
        sqlMenu.add(saveTextMenuItem);
        menuBar.add(sqlMenu); // Add Sql menu next to the existing menus
    }

    private void saveSelectedText(String selectedText) {
        SQlite sqlite = new SQlite("jdbc:sqlite:DeepL.db");
        sqlite.saveSelectedText(selectedText);
    }

    private void saveSelectedTextToDatabase() {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            // Establish connection to your SQL database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database_name", "username", "password")) {
                String sql = "INSERT INTO saved_text (text) VALUES (?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, selectedText);
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Selected text saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to save selected text.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error saving text to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No text selected to save.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void LanguageMenu() { //this is where the user can select a language to translate to
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Translate to");
        JMenuItem english = createMenuItem("English", "en");
        JMenuItem german = createMenuItem("German", "de");
        JMenuItem french = createMenuItem("French", "fr");
        JMenuItem spanish = createMenuItem("Spanish", "es");
        JMenuItem italian = createMenuItem("Italian", "it");
        JMenuItem dutch = createMenuItem("Dutch", "nl");

        menu.add(english);
        menu.add(german);
        menu.add(french);
        menu.add(spanish);
        menu.add(italian);
        menu.add(dutch);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void Choose() {
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose an action",
                "Choice",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"DeepL Service", "AI-Translation"},
                null
        );

        if (choice == 0) {
            handleApiKey();
            translationMethod = "DeepL"; // Set to DeepL translation method
        } else if (choice == 1) {
            translationMethod = "AI"; // Set to AI translation method
        }
    }


    private JMenuItem createMenuItem(String label, String languageCode) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.putClientProperty("languageCode", languageCode);
        menuItem.addActionListener(e -> {
            try {
                String selectedLanguageCode = (String) menuItem.getClientProperty("languageCode");
                Translator.setTargetLanguage(selectedLanguageCode);
                targetLanguageCode = selectedLanguageCode; // Store the selected target language code
                JMenu topLevelMenu = getMenuByLabel();
                topLevelMenu.setText("Language: " + menuItem.getText());

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        return menuItem;
    }

    private JMenu getMenuByLabel() {  //this a label manager for the language menu
        JMenuBar menuBar = getJMenuBar();
        return menuBar.getMenu(0);
    }
        private void Main_Text_Area() { // text area
        JPanel topPanel = new JPanel();
        textArea = new JTextArea();
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }



    public void Translate_Button() {
        JButton translateButton = new JButton("Translate");

        add(translateButton, BorderLayout.SOUTH);
        translateButton.addActionListener(e -> {
            try {
                if (translationMethod.equals("AI")) {
                    Translate_Ai();
                } else if (translationMethod.equals("DeepL")) {
                    Translate_DeepL();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void LookUp_Button() { // lookup button
        JButton lookUpButton = new JButton("Look_Up");


        add(lookUpButton, BorderLayout.NORTH);
        lookUpButton.addActionListener(e -> {
            try {
                LookUpSelectedWord();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }




    public void Translate_Ai() {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            try {
                // Get the path to the bundled Python executable
                String pythonExecutable = "dist/Translate.exe";

                // Run the bundled Python executable with the appropriate arguments
                ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, selectedText, "en", "DE"); //will rebuild the script, in order to make it work with the language menu

                processBuilder.redirectErrorStream(true); // Redirect error stream to capture errors

                // Start the Python process
                Process process = processBuilder.start();

                // Capture output from Python script
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder outputText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    outputText.append(line).append("\n");
                }

                // Wait for the Python script to finish
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    // Get the translated text from the Python script's output
                    String translatedText = outputText.toString().trim();

                    // Update the text area in your GUI with the translated text
                    textArea.setText(translatedText);
                } else {
                    // Handle error if Python script exits with non-zero status
                    JOptionPane.showMessageDialog(null, "Error executing Python script. Please check your script and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | InterruptedException e) {
                // Handle exceptions
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error executing Python script: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No text selected.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    public void Translate_DeepL() {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            try {


                // Ensure the API key is set before proceeding
                if (apiKey == null || apiKey.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "API key not set.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Set the text and target language for translation
                DeepLApiBuilder.setText(selectedText);
                Translator translator = new Translator("https://api-free.deepl.com/v2/translate?", apiKey, text);
                String translatedText = Translator.ParseText(translator.Connection()); // Store translated text

                // Replace the selected text with the translated text in the text area
                textArea.replaceSelection(translatedText);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, e.g., show an error message
                JOptionPane.showMessageDialog(null, "Error occurred during translation: " + e.getMessage(), "Translation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void LookUpSelectedWord() throws Exception {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            // Use the translated text stored in the instance variable
            DictionaryApi.setText(selectedText);
            DictJson dictJson = new DictJson("", selectedText, "en", "https://api.dictionaryapi.dev/api/v2/entries/en_US/");
            String definition = DictJson.ParseText(dictJson.Connection());
            JOptionPane.showMessageDialog(null, definition, "Definition", JOptionPane.INFORMATION_MESSAGE);

        }
    }


    public static void handleApiKey() {
        int option = JOptionPane.showOptionDialog(null, "Choose an action", "API Key", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Set API Key", "Get API Key", "Cancel"}, null);

        if (option == 0 || option == 1) { // Set or Get API Key
            JFileChooser fileChooser = new JFileChooser();
            int fileChooserResult = fileChooser.showOpenDialog(null);

            if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String apiKeyFilePath = selectedFile.getAbsolutePath();

                if (option == 0) { // Set API Key
                    apiKey = JOptionPane.showInputDialog("Enter the API key");
                } else { // Get API Key
                    try (BufferedReader reader = new BufferedReader(new FileReader(apiKeyFilePath))) {
                        apiKey = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error reading API key from file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                    }
                }
            }
        }
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
               try {
                      new LearnLanguage().setVisible(true);
                } catch (Exception e) {
                     e.printStackTrace();
                }
        });
    }
}

