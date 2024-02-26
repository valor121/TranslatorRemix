package DeepLApi;

import ApiKey.ApiKeyBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DeepLApiBuilder extends ApiKeyBuilder {
    public static String text;
    public static String targetLang;

    public static String apiKey;



    public DeepLApiBuilder(String url, String apiKey, String text, String targetLang) {
        super(url, apiKey);
        DeepLApiBuilder.text = text;
        DeepLApiBuilder.targetLang = targetLang;;
    }



    public static void setText(String text) {
        DeepLApiBuilder.text = text;
    }

    public static void setTargetLanguage(String targetLang) {
        DeepLApiBuilder.targetLang = targetLang;
    }


    @Override
    public void setUrl(String url) {

    }

    @Override
    public InputStream connection(String text) throws Exception {
        return null;
    }


    @Override
    public void SetApiKey(String file){
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String apiKey = bufferedReader.readLine(); // Read API key from the specified file

            // Write API key to "apikey.properties" without overwriting previous content
            Properties properties = new Properties();
            properties.setProperty("apiKey", apiKey);
            FileWriter fileWriter = new FileWriter("apikey.properties", true); // Append mode
            properties.store(fileWriter, "API Key");

            fileWriter.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void GetApiKey(String file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            apiKey = bufferedReader.readLine();

            FileWriter fileWriter = new FileWriter("apikey.properties");
            fileWriter.write(apiKey);
            fileWriter.close();

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InputStream Connection() throws Exception {
        if (apiKey == null) {
            throw new IllegalStateException("API key is not set");
        }

        String baseUrl = "https://api-free.deepl.com/v2/translate";
        String urlParams = "auth_key=" + apiKey + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) + "&target_lang=" + targetLang;

        URI uri = new URI(baseUrl +"?"+ urlParams);
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        System.out.println(apiKey);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            throw new Exception("Error: " + responseCode);

        }
    }
}
