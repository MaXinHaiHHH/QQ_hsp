package qqclient.service;

import java.util.HashMap;

/**
 * 该类管理客户端连接到服务器端的线程的类
 */
public class ManageClientConnectServerThread {
    //我们把多个线程放入到HashMap集合，key就是用户id，value就是线程
    private static HashMap<String,ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合
    public static void addCilentConnetServerThread(String userId,ClientConnectServerThread clientConnectServerThread){
        hm.put(userId,clientConnectServerThread);
    }
    //通过userId得到对应的线程
    public static ClientConnectServerThread  getCilentConnetServerThread(String userId){
        return hm.get(userId);
    }
}
