package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.*;

/**
 * 该类用于实现文件传输功能
 */
public class FileClientService {
    /**
     * 该类用于实现文件的传输
     * @param src 预传输文件路径
     * @param dest 传输对方接受路径
     * @param senderId 发送者
     * @param getterId 接受者
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId){
        Message message = new Message();
        message.setGetter(getterId);
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setDest(dest);
        message.setSrc(src);

        FileInputStream fileInputStream = null;

        byte[] bytes = new byte[(int)new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(bytes);//将文件写入到程序中
            message.setFileBytes(bytes);//将文件保存到message对象中
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream!=null){
                try {
                    //关闭
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //发送message对象到服务器
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getCilentConnetServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println("文件发送完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
