package fr.zoski.old;

import fr.zoski.old.EchoServer;

import java.io.IOException;

public class MainServer {

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
