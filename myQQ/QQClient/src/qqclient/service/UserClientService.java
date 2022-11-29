package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {

    //因为可能在其他地方要使用user信息，因此做成成员属性
    private User u = new User();
    ////因为可能在其他地方要使用socket信息，因此做成成员属性
    private Socket socket;

    //根据userId和pwd 到服务器验证该用户是否合法
    public boolean checkUser(String userId ,String pwd){
        boolean b = false;
        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);

        try {
            //连接到服务端，发送u对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"),9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送user对象

            //读取从服务端回送的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message)ois.readObject();

            if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){//登陆成功

                //创建一个和服务器端保持通信的线程->创建一个类ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServerThread.start();
                //这里为了客户端的扩展，将线程放到集合管理
                ManageClientConnectServerThread.addCilentConnetServerThread(userId,clientConnectServerThread);
                b = true;
            }else {
                //如果登录失败，就不能启动和服务器通信的线程，关闭socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList(){

        //发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setSender(u.getUserId());
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);

        //发送给服务器
        try {
            //应该得到当前线程的socket 对应的 ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getCilentConnetServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);//发送一个message对象，向服务器要求在线用户列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //退出客户端，向服务器请求退出线程
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定是哪个客户端退出
        //发送给服务器
        try {
            //应该得到当前线程的socket 对应的 ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getCilentConnetServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);//发送一个message对象，向服务器要求关闭线程
            System.out.println(u.getUserId() + " 退出系统 ");
            System.exit(0);//结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
