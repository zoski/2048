package fr.zoski;

import java.io.IOException;

/**
 * Created by gael on 22/12/15.
 */
public class MainClient {

    public static void main(String[] args) {

        EchoClient client = new EchoClient();
        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
