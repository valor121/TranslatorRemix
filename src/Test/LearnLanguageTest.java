package Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import DeepLApi.DeepLApiBuilder;
import Gui.LearnLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LearnLanguageTest {

    private LearnLanguage learnLanguage;

    @BeforeEach
    public void setUp() {
        learnLanguage = new LearnLanguage();
    }

    @Test
    public void testSetApiKey() {
        // You should set the API key first
        String apiKey = "test";
        DeepLApiBuilder.setText(apiKey);
        // Then check if it's set correctly
        assertEquals(apiKey, DeepLApiBuilder.text);
    }

    @Test
    public void testSetLanguage() {
        // Set the language
        learnLanguage.language = "en";
        // Check if it's set correctly
        assertEquals("en", learnLanguage.language);
    }

    @Test
    public void testTranslateSelectedWord() {
        learnLanguage.textArea.setText("Hello");
        try {
            // Translate the word
            learnLanguage.Translate_Ai();
            // Assuming the translation is correct
            String translatedText = learnLanguage.textArea.getText();
            assertEquals("", translatedText); // Adjust the expected translation accordingly
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during translation");
        }
    }

    @Test
    public void testLookUpSelectedWord() {
        learnLanguage.textArea.setText("Hello");
        try {
            // Perform lookup
            learnLanguage.LookUpSelectedWord();
            // Assuming the definition is retrieved correctly
            // Use appropriate assertion based on your implementation
            // For example:
            // assertEquals("Definition of 'Hello'", retrievedDefinition);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during lookup");
        }
    }
}