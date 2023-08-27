package DeepLApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Translator extends DeepLApiBuilder {

    public Translator(String url, String apiKey, String text) throws Exception {
        super(url, apiKey, text, targetLang);
        InputStream inputStream = Connection();
        ParseText(inputStream);
    }
    public static String ParseText(Object inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray translationsArray = jsonResponse.getJSONArray("translations");
        JSONObject translationObject = translationsArray.getJSONObject(0);
        String translatedText = translationObject.getString("text");

        System.out.println(translatedText); // or do whatever you want with the translated text
        return translatedText;
    }
}


