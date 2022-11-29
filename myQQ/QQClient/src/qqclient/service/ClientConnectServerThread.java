package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnectServerThread extends Thread {
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接受一个Socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    //
    @Override
    public void run() {
        //因为Thread需要在后台和服务器通信，因此我们while循环
        while (true) {

            try {
                //System.out.println("客户端线程，等待从读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送Message对象,线程会阻塞在这里
                //Message message = (Message) ois.readObject();
                //注意，后面我们需要去使用message
                //判断这个message类型，然后做相应的业务处理
                    Object o = ois.readObject();//如果服务器没有发送objec对象,线程会阻塞在这里
                    if (o instanceof ArrayList) {
                        ArrayList<Message> messageArrayList = (ArrayList<Message>) o;
                        System.out.println("以下是您的离线消息：⬇");
                        for (Message message : messageArrayList) {
                            System.out.println(message.getSender() + ": " + message.getContent());
                        }
                    }
                    else {
                        Message message = (Message) o;
                        //如果是读取到的是 服务端返回的在线用户列表
                        if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                            //取出在线列表信息，并显示
                            //规定
                            String[] onlineUsers = message.getContent().split(" ");
                            System.out.println("\n=======当前在线用户列表========");
                            for (int i = 0; i < onlineUsers.length; i++) {
                                System.out.println("用户: " + onlineUsers[i]);
                            }
                            System.out.print("请输入你的选择：");

                        } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {//普通的聊天消息
                            //把从服务器转发的消息，显示到控制台即可
                            System.out.println("\n" + message.getSender()
                                    + " 对 " + message.getGetter() + " 说: " + message.getContent());
                            System.out.print("请输入你的选择：");
                        } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                            //显示在客户端的控制台
                            System.out.println("\n" + message.getSender() + " 对大家说: " + message.getContent());
                            System.out.print("请输入你的选择：");
                        } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                            //显示保存的路径
                            System.out.println("\n" + message.getSender() + "给" + message.getGetter()
                                    + "发送文件，保存到：" + message.getDest());
                            //取出message对象中的文件数组，通过文件输出流写出到磁盘
                            FileOutputStream fileOutputStream = new FileOutputStream(message.getDest(), true);
                            fileOutputStream.write(message.getFileBytes());
                            fileOutputStream.close();
                            System.out.println("\n文件保存成功");
                        } else {
                            System.out.println("是其他类型的message, 暂时不处理....");
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //为了更方便的得到Socket
    public Socket getSocket() {
        return socket;
    }
}
