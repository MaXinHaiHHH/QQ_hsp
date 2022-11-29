package qqclient.view;

import qqclient.service.FileClientService;
import qqclient.service.MessageClientService;
import qqclient.service.UserClientService;
import qqclient.utils.Utility;

/**
 * 客户端的菜单界面
 */
public class QQView {
    private boolean loop = true; //控制是否显示菜单
    private String key =""; //用来获取客户的键盘输入
    private UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户
    private MessageClientService messageClientService = new MessageClientService();//对象是用于私聊、群聊
    private FileClientService fileClientService = new FileClientService();//对象是用于文件的传输

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统...");
    }
    //显示主菜单
    private void mainMenu(){

        while (loop){
            System.out.println("=========欢迎登录陆网络通信系统===========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);

            //根据用户的输入来处理不同的逻辑
            switch (key){
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密 码：");
                    String pwd = Utility.readString(50);
                    //需要到服务端去验证该用户是否合法
                    //这里有很多代码，我们这里编写了一个类UserClientService[用户登录/注册]
                    if(userClientService.checkUser(userId,pwd)){//还没写完
                        System.out.println("=========欢迎(用户"+userId+")========");
                        //进入二级菜单
                        while (loop){
                            System.out.println("\n========网络通信系统二级菜单(用户"+userId+")=========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    //用来获取在线用户列表的方法
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.print("请输入相对大家说的话：");
                                    String tell = Utility.readString(100);
                                    //调用一个方法，将消息封装成message对象，发送给服务端
                                    messageClientService.sendMessageToAll(tell,userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号(在线):");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话：");
                                    String content = Utility.readString(100);
                                    //方法，将私聊消息发送给服务端
                                    messageClientService.sendMessageToOne(content,userId,getterId);
                                    break;
                                case "4":
                                    System.out.print("请输入传输文件所在地址：");
                                    String src = Utility.readString(50);
                                    System.out.print("请输入想保存在对方什么位置：");
                                    String dest = Utility.readString(50);
                                    System.out.print("请输入想传输文件的用户号(在线):");
                                    String getterid = Utility.readString(50);
                                    fileClientService.sendFileToOne(src,dest,userId,getterid);
                                    break;
                                case "9":
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    }else {//用户登录失败
                        System.out.println("========登陆失败========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
