package Test;

import Gui.LearnLanguage;
import SqlLite.SQlite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.IOException;

public class LearnLanguageTest {

    private LearnLanguage learnLanguage;

    @BeforeEach
    public void setUp() throws IOException {
        learnLanguage = new LearnLanguage();
    }


    @Test
    public void testSetApiKey() {
        SQlite sQlite = new SQlite("jdbc:sqlite:DeepL.db");
        String sql;
        sql = "SELECT * From DeepL WHERE ApiKey = test  LIMIT 1";
        String key ="";

        try {
            Assertions.assertEquals("test", key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetLanguage() {
        learnLanguage.language = "en";
        Assertions.assertEquals("en", learnLanguage.language);
    }

    @Test
    public void testTranslateSelectedWord() {
        learnLanguage.textArea.setText("Hello");
        try {
            learnLanguage.Translate_Ai();
            Assertions.assertEquals("Hello", JOptionPane.showInputDialog("Translation"));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during translation");
        }
    }

    @Test
    public void testLookUpSelectedWord() {
        learnLanguage.textArea.setText("Hello");
        try {
            learnLanguage.Translate_Button();
            learnLanguage.LookUpSelectedWord();
            // Assuming that the lookup retrieves a definition for "Hallo"
            Assertions.assertEquals("Hello", JOptionPane.showInputDialog("Definition"));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during lookup");
        }
    }
}
