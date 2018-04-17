package com.example.spark;

import com.example.spark.utils.SparkTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public abstract class AbstractTestMain {

    public void overrideSystemTruststore() {
        //
        // Handle SSL stuff.  This is for local testing only!
        //

        String certificatesTrustStorePath = "./keystore.jks";
        System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
    }

    public void restoreSystemTruststore() {
        Properties props  = System.getProperties();
        props.remove("javax.net.ssl.trustStore");
        System.setProperties(props);
    }

    @AfterEach
    public void cleanup() {
        SparkTestUtils.stopServer();
    }

    @Test
    public void testDefaultConfig() throws InterruptedException {
        String[] args = new String[0];
        doTest(args, "http://localhost:8888");
    }

    @Test
    public void testSslConfig() throws InterruptedException {
        overrideSystemTruststore();

        String[] args = new String[1];
        args[0] = "8443";

        doTest(args, "https://localhost:8443");

        restoreSystemTruststore();
    }

    @Test
    public void testArbitraryConfig() throws InterruptedException {
        String[] args = new String[1];
        args[0] = "8887";
        doTest(args, "http://localhost:8887");
    }

    private void doTest(String[] config, String baseUrl) throws InterruptedException {
        Main.main(config);
        Thread.sleep(500);  // Arbitrary wait to complete spark initialization
        SparkTestUtils.initialize(baseUrl);

        SparkTestUtils.TestResponse res = SparkTestUtils.request("GET", "/ping", "");
        Assertions.assertEquals("<xml>pong</xml>", res.body);
    }

}