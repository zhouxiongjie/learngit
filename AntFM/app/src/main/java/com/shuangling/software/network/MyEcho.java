/*
 * Echo.java
 * MrBin99 © 2018
 */
package com.shuangling.software.network;

import android.util.Log;

import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

/**
 * This class is the primary API for interacting with broadcasting.
 */
public final class MyEcho {

    /**
     * The broadcasting connector.
     */
    private MySocketIOConnector connector;

    /**
     * Creates a new Echo instance with default options.
     */
    public MyEcho() {
        this(new EchoOptions());
    }

    /**
     * Create a new Echo instance.
     *
     * @param options options
     */
    public MyEcho(EchoOptions options) {
        this.connector = new MySocketIOConnector(options);
    }


    public void send(final Object... args){
        this.connector.send(args);
    }

    /**
     * Connect to the Echo server.
     *
     * @param success success callback
     * @param error   error callback
     */
    public void connect(EchoCallback success, EchoCallback error) {
        connector.connect(success, error);
    }

    /**
     * Listen for general event on the socket.
     *
     * @param eventName event name
     * @param callback  callback
     * @see io.socket.client.Socket list of event types to listen to
     */
    public void on(String eventName, EchoCallback callback) {
        connector.on(eventName, callback);
    }

    /**
     * Remove all listeners for a general event.
     *
     * @param eventName event name
     */
    public void off(String eventName) {
        connector.off(eventName);
    }

    /**
     * Listen for an event on a channel instance.
     *
     * @param channel  channel name
     * @param event    event name
     * @param callback callback when event is triggered
     * @return the channel
     */
    public MySocketIOChannel listen(String channel, String event, EchoCallback callback) {
        return connector.listen(channel, event, callback);
    }

    /**
     * Get a channel by name.
     *
     * @param channel channel name
     * @return the channel
     */
    public MySocketIOChannel channel(String channel) {
        return (MySocketIOChannel) connector.channel(channel);
    }

    /**
     * Get a private channel by name.
     *
     * @param channel channel name
     * @return the channel
     */
    public MySocketIOPrivateChannel privateChannel(String channel) {
        return (MySocketIOPrivateChannel) connector.privateChannel(channel);
    }

    /**
     * Get a presence channel by name.
     *
     * @param channel channel name
     * @return the channel
     */
    public MySocketIOPresenceChannel presenceChannel(String channel) {
        return (MySocketIOPresenceChannel) connector.presenceChannel(channel);
    }

    /**
     * Leave the given channel.
     *
     * @param channel channel name
     */
    public void leave(String channel) {
        connector.leave(channel);
    }

    /**
     * @return if currently connected to server.
     */
    public boolean isConnected() {
        return connector.isConnected();
    }

    /**
     * Disconnect from the Echo server.
     */
    public void disconnect() {
        try{
            connector.disconnect();
        }catch (Exception e){
            Log.e("test",e.toString());
        }

    }
}
