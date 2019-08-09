// IAudioPlayer.aidl
package com.shuangling.software.service;

import com.shuangling.software.entity.Audio;
import com.shuangling.software.entity.AudioDetail;
import com.shuangling.software.entity.AudioInfo;

// Declare any non-default types here with import statements

interface IAudioPlayer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    //void playAudio();


//    void setPlayerList(in List<Audio> list);

    void setPlayerList(in List<AudioInfo> list);

//    List<Audio> getPlayerList();

    List<AudioInfo> getPlayerList();

    AudioInfo getCurrentAudio();

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

//    void playAudio(in Audio audio);
//    void playAudioDetail(in AudioDetail audio);
    void playAudio(in AudioInfo audio);

    void setTimerType(in int type);

    int getTimerType();

    void showFloatPlayer();

    void next();

    void previous();
}
