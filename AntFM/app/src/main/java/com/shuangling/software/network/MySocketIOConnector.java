/*
 * SocketIOConnector.java
 * MrBin99 © 2018
 */
package com.shuangling.software.network;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoException;
import net.mrbin99.laravelechoandroid.EchoOptions;
import net.mrbin99.laravelechoandroid.channel.AbstractChannel;
import net.mrbin99.laravelechoandroid.connector.AbstractConnector;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * This class creates a connector to a Socket.io server.
 */
public class MySocketIOConnector extends AbstractConnector {



    /**
     * The socket.
     */
    private Socket socket;

    /**
     * All of the subscribed channel names.
     */
    private Map<String, MySocketIOChannel> channels;

    /**
     * Create a new Socket.IO connector.
     *
     * @param options options
     */
    public MySocketIOConnector(EchoOptions options) {
        super(options);

        channels = new HashMap<>();
    }

    @Override
    public void connect(EchoCallback success, EchoCallback error) {
        try {
            socket = IO.socket(this.options.host);
            socket.connect();

            if (success != null) {
                socket.on(Socket.EVENT_CONNECT, success);
            }

            if (error != null) {
                socket.on(Socket.EVENT_CONNECT_ERROR, error);
            }
        } catch (URISyntaxException e) {
            if (error != null) {
                error.call();
            }
        }

    }



    public void send(final Object... args){
        socket.send(args);
    }

    /**
     * Listen for general event on the socket.
     *
     * @param eventName event name
     * @param callback  callback
     * @see io.socket.client.Socket list of event types to listen to
     */
    public void on(String eventName, EchoCallback callback) {
        socket.on(eventName, callback);
    }

    /**
     * Remove all listeners for a general event.
     *
     * @param eventName event name
     */
    public void off(String eventName) {
        socket.off(eventName);
    }

    /**
     * Listen for an event on a channel.
     *
     * @param channel  channel name
     * @param event    event name
     * @param callback callback
     * @return the channel
     */
    public MySocketIOChannel listen(String channel, String event, EchoCallback callback) {
        return (MySocketIOChannel) this.channel(channel).listen(event, callback);
    }

    @Override
    public AbstractChannel channel(String channel) {
        if (!channels.containsKey(channel)) {
            channels.put(channel, new MySocketIOChannel(socket, channel, options));
        }
        return channels.get(channel);
    }

    @Override
    public AbstractChannel privateChannel(String channel) {
        String name = "private-" + channel;

        if (!channels.containsKey(name)) {
            channels.put(name, new MySocketIOPrivateChannel(socket, name, options));
        }
        return channels.get(name);
    }

    @Override
    public AbstractChannel presenceChannel(String channel) {
        String name = "presence-" + channel;

        if (!channels.containsKey(name)) {
            channels.put(name, new MySocketIOPresenceChannel(socket, name, options));
        }
        return channels.get(name);
    }

    @Override
    public void leave(String channel) {
        String privateChannel = "private-" + channel;
        String presenceChannel = "presence-" + channel;

        for (String subscribed : channels.keySet()) {
            if (subscribed.equals(channel) || subscribed.equals(privateChannel) || subscribed.equals(presenceChannel)) {
                try {
                    channels.get(subscribed).unsubscribe(null);
                } catch (EchoException e) {
                    e.printStackTrace();
                }

                channels.remove(subscribed);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return socket.connected();
    }

    @Override
    public void disconnect() {
        for (String subscribed : channels.keySet()) {
            try {
                channels.get(subscribed).unsubscribe(null);
            } catch (EchoException e) {
                e.printStackTrace();
            }
        }

        channels.clear();
        socket.disconnect();
    }
}
