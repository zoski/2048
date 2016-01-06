package fr.zoski.client;

import java.nio.channels.SocketChannel;

/**
 * Created by scrutch on 05/01/16.
 */
public class ClientDataEvent {

    public Client client;
    public SocketChannel socket;
    public byte[] data;

    public ClientDataEvent(Client client, SocketChannel socket, byte[] data) {
        this.client = client;
        this.socket = socket;
        this.data = data;
    }
}
