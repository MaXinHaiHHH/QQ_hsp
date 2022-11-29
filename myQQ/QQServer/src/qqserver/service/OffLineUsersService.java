package qqserver.service;

import qqcommon.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class OffLineUsersService{
    private static ConcurrentHashMap<String, ArrayList<Message>> offlinedb = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ArrayList<Message>> getOfflinedb() {
        return offlinedb;
    }

    public void setOfflinedb(ConcurrentHashMap<String, ArrayList<Message>> offlinedb) {
        OffLineUsersService.offlinedb = offlinedb;
    }
    //判断用户是否有离线消息
    public static boolean determine(String userid){
        if(offlinedb.containsKey(userid)) {//如果用户有离线消息返回true,否则返回false
            return true;
        }else
            return false;
    }
    //将离线消息添加到ConcurrentHashMap集合中
    public void addOfflineMessage(Message message){
        if(!offlinedb.containsKey(message.getGetter())){//如果该用户第一次接收离线消息
            ArrayList<Message> arrayList = new ArrayList();
            arrayList.add(message);//将消息添加到message中
            offlinedb.put(message.getGetter(),arrayList);//将用户信息和消息保存到ConcurrentHashMap集合中

        }else {
            ArrayList<Message> arrayList = offlinedb.get(message.getGetter());
            arrayList.add(message);
        }
    }
    //用户上线后，从ConcurrentHashMap集合中取出values(ArrayList集合)返回给客户端
    public void returnOfflineMessage(String userid,ConcurrentHashMap concurrentHashMap){
        if (offlinedb.containsKey(userid)) {
            try {
                ArrayList<Message> arrayList = (ArrayList<Message>) offlinedb.get(userid);
                OutputStream os = ManageClientThreads.getServerConnectClientThread(userid).getSocket().getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                //将message集合发送到客户端
                oos.writeObject(arrayList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("用户不存在，发送失败...");
        }
    }
    //将离线信息从ConcurrentHashMap集合中删除
    public void deleteOfflienMessage(String userid){
            offlinedb.remove(userid);
    }
}
