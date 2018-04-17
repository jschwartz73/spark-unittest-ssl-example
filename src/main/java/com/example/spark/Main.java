package com.example.spark;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        int port = 8888;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        port(port);

        //
        // Enable SSL
        //
        if (port == 8443) {
            String keystoreFilePath = "./keystore.jks";
            String keystorePassword = "password";
            String truststoreFilePath = null;
            String truststorePassword = null;
            secure(keystoreFilePath, keystorePassword, truststoreFilePath, truststorePassword);
        }

        get("/ping", (sparkReq, sparkRes) -> {
            sparkRes.type("text/xml");
            return "<xml>pong</xml>";
        });
    }

}