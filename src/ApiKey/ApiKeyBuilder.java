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

    public InputStream executeApi(String endpoint) throws Exception { // Execute the API call with the given endpoint and return the InputStream of the response body as a result of the call to the API
        //End point is the part of the url that comes after the base url, text= and target_lang= are examples of endpoints. The endpoint is the part of the url that is specific to the API call.
        String urlParams = constructUrlParams(endpoint) + constructAdditionalParams();
        String fullUrl = baseUrl + "?" + urlParams + "&auth_key=" + apiKey;
        return Connection(fullUrl);
    }

    protected String constructUrlParams(String endpoint) { // Provide a default implementation for url parameters
        return endpoint;
    }

    protected String constructAdditionalParams() { // Provide a default implementation for additional parameters
        // Provide a default implementation for additional parameters
        return "";
    }

    public abstract InputStream Connection() throws Exception;
}
