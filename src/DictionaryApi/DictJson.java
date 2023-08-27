package DictionaryApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DictJson extends DictionaryApi {

    public DictJson(String apiKey, String word, String language, String url) {
        super(apiKey, word, language, url);
    }

    public static String ParseText(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONArray jsonArray = new JSONArray(response.toString());
        StringBuilder definitionsBuilder = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject entryObject = jsonArray.getJSONObject(i);
            JSONArray definitionsArray = entryObject.getJSONArray("meanings");

            for (int j = 0; j < definitionsArray.length(); j++) {
                JSONObject meaningObject = definitionsArray.getJSONObject(j);
                JSONArray definitions = meaningObject.getJSONArray("definitions");

                for (int k = 0; k < definitions.length(); k++) {
                    JSONObject definitionObject = definitions.getJSONObject(k);
                    String definition = definitionObject.getString("definition");
                    definitionsBuilder.append(definition).append("\n");
                }
            }
        }

        return definitionsBuilder.toString();
    }
}