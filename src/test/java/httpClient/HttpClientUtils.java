package httpClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

    public static HttpResponse getGetResponse(String schema, String host, String path, String[] queries){
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();

            URIBuilder uri = new URIBuilder();
            uri.setScheme(schema);
            uri.setHost(host);
            uri.setPath(path);

            if (queries != null) {
                for (String query : queries) {
                    uri.setCustomQuery(query); // String[] queries -> can add multiple queries, uri.setCustomQuery will automatically add "?"
                }
            }

            HttpGet httpGet = new HttpGet(uri.build());
            httpGet.setHeader("Accept", "application/json");

            return httpClient.execute(httpGet);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static Map<String, Object> getResponseBody(HttpResponse response){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> deserializedObject = objectMapper.readValue(response.getEntity().getContent(),
                    new TypeReference<Map<String, Object>>() {});

            return deserializedObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Map<String, Object>> getResponseBodyAsList(HttpResponse response){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Map<String, Object>> deserializedObject = objectMapper.readValue(response.getEntity().getContent(),
                    new TypeReference<List<Map<String, Object>>>() {});

            return deserializedObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpResponse getGetResponseByURI(String endpoint){
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpGet httpGet = new HttpGet(endpoint);
            httpGet.setHeader("Accept", "application/json");

            return httpClient.execute(httpGet);
        } catch (IOException e) {
            return null;
        }
    }

}
