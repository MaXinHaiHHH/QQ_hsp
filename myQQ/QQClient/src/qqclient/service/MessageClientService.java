package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 该对象提供和消息相关的服务方法
 */
public class MessageClientService {
    /**
     *该方法用于用户发送私聊消息
     * @param content 内容
     * @param senderId 发送用户Id
     * @param getterId 接受用户Id
     */
    public void sendMessageToOne(String content,String senderId,String getterId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);//普通的消息
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());//发送时间设置到message对象
        //System.out.println(senderId + "对" + getterId +"说" + content);

        //发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getCilentConnetServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法用于用户发送群聊消息
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content,String senderId){
        //构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//群发消息
        message.setContent(content);
        message.setSender(senderId);
        message.setSendTime(new java.util.Date().toString());//发送时间设置到message对象

        //发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getCilentConnetServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
