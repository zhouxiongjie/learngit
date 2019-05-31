// IAudioPlayer.aidl
package com.shuangln.antfm.service;

import com.shuangln.antfm.entity.Audio;


// Declare any non-default types here with import statements

interface IAudioPlayer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    //void playAudio();


    void setPlayerList(in List<Audio> list);

    List<Audio> getPlayerList();

    Audio getCurrentAudio();

    void setScreenBrightness(in int brightness);

    void setMuteMode(in boolean bMute);

    void setVolume(int volume);

    void setAutoPlay(in boolean autoPlay);

    void setPlaySpeed(in int speed);

    int getPlaySpeed();

    long getDuration();

    long getCurrentPosition();

    void changeQuality(in String quality);

    void setCirclePlay(in boolean circle);

    void seekTo(in int ms);

    void replay();

    void pause();

    void stop();

    void start();

    int getPlayerState();

    void playAudio(in Audio audio);

    void setTimerType(in int type);

    int getTimerType();

    void showFloatPlayer();
}
