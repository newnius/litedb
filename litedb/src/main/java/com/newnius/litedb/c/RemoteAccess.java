/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newnius.litedb.c;

import com.newnius.litedb.m.Record;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;

/**
 *
 * @author Newnius
 */
public class RemoteAccess extends Thread{
    private final Socket socket;
    public RemoteAccess(Socket socket){
        this.socket=socket;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));//获得客户端的输入流
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);//获得客户端输出流)
            if (socket.isConnected()) {
                System.out.println("Accept " + socket.getInetAddress().getHostAddress());
                out.println("Welcome");
            }

            while (Global.isDBPrepared&&Global.isRemoteAccessAvailable) {
                String query = reader.readLine();
                System.out.println("Received：" + query);
                if(query.toLowerCase().equals("quit;")){
                    break;
                } else {
                    out.println(handle(query));
                }
            }
            out.println("Connection is closing.");
            socket.close();
        } catch (Exception e) {
            //Logger.getLogger(RemoteAccess.class.getName()).log(Level.INFO, null, e);
        }        
    }    
    
    private String handle(String query){
        
        return JSONArray.fromObject(sqlParser.parse(query)).toString();
    }
    
}
