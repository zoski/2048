package fr.zoski;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	    System.out.println("prout");

        EchoServer server = new EchoServer();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
