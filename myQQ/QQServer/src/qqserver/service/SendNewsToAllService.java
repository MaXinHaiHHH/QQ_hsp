package qqserver.service;


import qqcommon.Message;
import qqcommon.MessageType;
import utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SendNewsToAllService implements Runnable{
    @Override
    public void run() {
        //未来可以推送多次新闻，这里使用while
        while (true){
            System.out.println("请输入服务器要推送的消息(输入【exit】退出推送服务)：");
            String news = Utility.readString(1000);
            if(news.equals("exit")){
                System.out.println("推送消息系统关闭");
                break;
            }
            //构建一个消息类型，群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人，说：" + news);

            //遍历所有通信线程，得到socket，并发送message
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onlineUserId = iterator.next();
                try {
                    ObjectOutputStream oos =
                            new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
