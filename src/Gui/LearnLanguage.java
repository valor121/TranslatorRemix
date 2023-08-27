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

import static DeepLApi.DeepLApiBuilder.*;


public class LearnLanguage extends JFrame {
    private String translationMethod;
    public String language;
    private String translatedText = text;

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

        setVisible(true);
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




    public void Translate_Ai() throws IOException {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            try {
                // Python script path
                String pythonScript = "Translate.py";

                // Run the Python script using ProcessBuilder
                String[] cmd = { "python", pythonScript, selectedText,"fr" ,targetLanguageCode };
                ProcessBuilder processBuilder = new ProcessBuilder(cmd);
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();

                // Read the output from the Python script
                InputStream inputStream = process.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); // Specify the encoding as 'UTF-8'
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuilder outputText = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    outputText.append(line);
                }

                // Wait for the Python script to finish
                process.waitFor();

                // Get the translated text from the Python script's output
                String translatedText = outputText.toString();

                // Update the text area in your GUI with the translated text
                textArea.setText(translatedText);
            } catch (Exception e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }
    }

    public void Translate_DeepL() throws Exception {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            DeepLApiBuilder.setText(selectedText);
            Translator translator = new Translator("https://api-free.deepl.com/v2/translate?", apiKey, text);
            translatedText = Translator.ParseText(translator.Connection()); // Store translated text
            textArea.replaceSelection(translatedText);
        }
    }

    public void LookUpSelectedWord() throws Exception {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            // Use the translated text stored in the instance variable
            DictionaryApi.setText(translatedText);
            DictJson dictJson = new DictJson("", translatedText, "en", "https://api.dictionaryapi.dev/api/v2/entries/en_US/");
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
                    String apiKeyFile = JOptionPane.showInputDialog("Enter the API key file name");
                    DeepLApiBuilder.setText(apiKeyFile);

                } else { // Get API Key
                    DeepLApiBuilder deepLApiBuilder = new DeepLApiBuilder("", "", "", "");
                    deepLApiBuilder.SetApiKey(apiKeyFilePath);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No file selected.", "File Selection", JOptionPane.WARNING_MESSAGE);
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

