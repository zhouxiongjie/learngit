/*
 * SocketIOPrivateChannel.java
 * MrBin99 © 2018
 */
package com.shuangling.software.network;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoException;
import net.mrbin99.laravelechoandroid.EchoOptions;

import org.json.JSONObject;

import io.socket.client.Socket;

/**
 * This class represents a Socket.io private channel.
 */
public class MySocketIOPrivateChannel extends MySocketIOChannel {

    /**
     * Create a new Socket.IO private channel.
     *
     * @param socket  the socket
     * @param name    channel name
     * @param options Echo options
     */
    public MySocketIOPrivateChannel(Socket socket, String name, EchoOptions options) {
        super(socket, name, options);
    }

    /**
     * Trigger client event on the channel.
     *
     * @param event    event name
     * @param data     data to send
     * @param callback callback from the server
     * @return this channel
     * @throws EchoException if error creating the whisper
     */
    public MySocketIOPrivateChannel whisper(String event, JSONObject data, EchoCallback callback)
            throws EchoException {

        JSONObject object = new JSONObject();

        try {
            object.put("channel", getName());
            object.put("event", "client-" + event);

            if (data != null) {
                object.put("data", data);
            }

            if (callback != null) {
                socket.emit("client event", object, callback);
            } else {
                socket.emit("client event", object);
            }

        } catch (Exception e) {
            throw new EchoException("Cannot whisper o, channel '" + getName() + "' : " + e.getMessage());
        }

        return this;
    }
}
