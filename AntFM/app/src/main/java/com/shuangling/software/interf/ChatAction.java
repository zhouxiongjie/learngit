package com.shuangling.software.interf;



/**
 * 聊天界面的接口
 */
public interface ChatAction {


    /**
     * 发送图片
     *
     */
    void sendImage();


    /**
     * 连麦
     *
     */
    void joinRoom();

    /**
     * 发送文字
     *
     */
    void sendText(String str);


    void invite();

}
