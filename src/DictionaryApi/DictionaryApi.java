package DictionaryApi;

import ApiKey.ApiKeyBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DictionaryApi extends ApiKeyBuilder {

    private static String language;
    private static String word;

    public DictionaryApi(String apiKey, String word, String language, String url) {
        super(url, apiKey);
        DictionaryApi.word = word;
        DictionaryApi.language = language;
    }

    public static void setText(String selectedText) {
        DictionaryApi.word = selectedText;
    }


    @Override
    public void setUrl(String url) {
    }

    @Override
    public InputStream connection(String text) throws Exception {
        return null;
    }

    @Override
    public void SetApiKey(String apiKey) {
    }



    @Override
    public InputStream Connection() throws Exception {
        String baseUrl = "https://api.dictionaryapi.dev/api/v2/entries/" + language + "/" + word;
        URL url = new URL(baseUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            throw new Exception("Error: " + responseCode);
        }
    }
}

