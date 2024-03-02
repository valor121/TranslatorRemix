package ApiKey;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class ApiKeyBuilder {

    protected String baseUrl;
    protected String apiKey;

    public ApiKeyBuilder(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public abstract void setUrl(String url);

    public abstract InputStream connection(String text) throws Exception;

    public abstract void SetApiKey(String file);

    protected InputStream Connection(String urlParams) throws Exception { //The method that establishes the connection to the API.
        URL url = new URL(urlParams);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Authorization", "API-Auth-Key " + apiKey);
        conn.getOutputStream().write(urlParams.getBytes(StandardCharsets.UTF_8));

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            throw new Exception("Error: " + responseCode);
        }
    }



    public abstract InputStream Connection() throws Exception;
}
