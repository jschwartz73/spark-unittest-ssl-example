package com.example.spark.utils;

import org.apache.commons.io.IOUtils;
import org.junit.platform.commons.util.StringUtils;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;


public class SparkTestUtils {
    private static String baseUrl = "http://localhost:9193";

    public static void initialize(String baseUrl) {
        SparkTestUtils.baseUrl = baseUrl;
    }

    public static TestResponse request(String method, String path, String requestBody) {

        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-type", "text/xml");

            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
            connection.addRequestProperty("Accept", "text/html");

            connection.setDoOutput(true);
            if (StringUtils.isNotBlank(requestBody)) {
                IOUtils.write(requestBody.getBytes(), connection.getOutputStream());
            }

            connection.connect();

            String body = IOUtils.toString(connection.getInputStream(), "UTF-8");
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage() + ", server: " + baseUrl);
            return null;
        }
    }

    public static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }

    public static void stopServer() {
        Spark.stop();
        try {
            Spark.port();
            Thread.sleep(500);
        } catch (IllegalStateException e){
            // Expected when old spark server is finished shutting down
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}