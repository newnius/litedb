package com.newnius.litedb.c;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Newnius
 */
public class RemoteServer extends Thread {

    public RemoteServer() {

    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(1888);//创建服务器套接字
            System.out.println("Server opened, waiting for client.");
            while (Global.isDBPrepared&&Global.isRemoteAccessAvailable) {
                Socket socket = server.accept();//等待客户端连接
                new RemoteAccess(socket).start();
            }
            server.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.out.println("Server is closed");
    }
}
