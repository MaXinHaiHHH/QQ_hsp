package qqserver.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;


/**
 * 该类对应的对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String uesrId;//连接到服务端的用户Id
    OffLineUsersService offLineUsersService = new OffLineUsersService();

    public ServerConnectClientThread(Socket socket, String uesrId) {
        this.socket = socket;
        this.uesrId = uesrId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {//线程处于run的状态，可以发送/接受消息

        while (true){
            System.out.println("服务端和客户端"+ uesrId +"保持通信，读取数据...");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //后面会使用message，根据message的类型做相应的业务处理
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    //客户端要在线用户列表
                    System.out.println(message.getSender()+"要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回message
                    //构建一个message对象，返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //写入到数据通道,返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                }else if(message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){//客户端退出
                    System.out.println(message.getSender() + " 退出 ");
                    //将这个客户端对应的线程从集合中移除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();//关闭连接
                    //退出线程
                    break;
                }else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    //判断发送信息的用户是否在线
                    if(hm.containsKey(message.getGetter())) {
                        //如果用户在线,根据message获取getterid，然后在得到对应的线程
                        ServerConnectClientThread serverConnectClientThread =
                                ManageClientThreads.getServerConnectClientThread(message.getGetter());
                        //再得到对应socket的输出流，将message对象转发给指定的客户端
                        ObjectOutputStream oos =
                                new ObjectOutputStream(serverConnectClientThread.socket.getOutputStream());
                        oos.writeObject(message);//转发
                        }else{
                            System.out.println("用户目前不在线");
                            offLineUsersService.addOfflineMessage(message);//将消息添加到离线集合中
                        }
                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //需要遍历管理线程的集合，把所有的线程的socket得到，然后把message进行转发
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()){
                        //取出在线的用户的id
                        String onLineUserId = iterator.next();;

                        if(!onLineUserId.equals(message.getSender())){//将自己排除
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    //根据message获取getterid，然后在得到对应的线程
                    ServerConnectClientThread serverConnectClientThread =
                            ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    //再得到对应socket的输出流，将message对象转发给指定的客户端
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.socket.getOutputStream());
                    oos.writeObject(message);//将文件转发
                }
                else{
                    //暂时不处理
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
