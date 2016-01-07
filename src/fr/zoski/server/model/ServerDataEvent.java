package fr.zoski.server.model;

import fr.zoski.server.GameServer;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
    public GameServer server;
    public SocketChannel socket;
    public byte[] data;

    public ServerDataEvent(GameServer server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data = data;
    }
}