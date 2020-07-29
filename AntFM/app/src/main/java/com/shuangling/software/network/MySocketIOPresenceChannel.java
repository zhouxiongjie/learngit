/*
 * SocketIOPresenceChannel.java
 * MrBin99 © 2018
 */
package com.shuangling.software.network;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;
import net.mrbin99.laravelechoandroid.channel.IPresenceChannel;

import io.socket.client.Socket;

/**
 * This class represents a Socket.io presence channel.
 */
public class MySocketIOPresenceChannel extends MySocketIOPrivateChannel implements IPresenceChannel {

    /**
     * Create a new Socket.IO presence channel.
     *
     * @param socket  the socket
     * @param name    channel name
     * @param options Echo options
     */
    public MySocketIOPresenceChannel(Socket socket, String name, EchoOptions options) {
        super(socket, name, options);
    }

    @Override
    public IPresenceChannel here(EchoCallback callback) {
        on("presence:joining", callback);

        return this;
    }

    @Override
    public IPresenceChannel joining(EchoCallback callback) {
        on("presence:subscribed", callback);

        return this;
    }

    @Override
    public IPresenceChannel leaving(EchoCallback callback) {
        on("presence:leaving", callback);

        return this;
    }
}
