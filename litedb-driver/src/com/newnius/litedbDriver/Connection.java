package com.newnius.litedbDriver;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;

public class Connection {

    Socket socket;
    OutputStream out;
    BufferedReader reader;
    BufferedReader localMessage;

    public Connection() {
        try {
            Socket client = new Socket("192.168.40.1", 7096);//创建客户端套接字
            out = client.getOutputStream();//获取输出流
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));//获取输入流 获得服务器返回的数据
            localMessage = new BufferedReader(new InputStreamReader(System.in));//接受客户端从键盘输入的信息
            System.out.println(reader.readLine());
        } catch (IOException e) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean execute(String sql) {
        try {
            String res;
            sql += "\n";
            out.write(sql.getBytes());//传输给服务器
            res = reader.readLine();
            //System.out.println(res);
            //JSONArray json = JSONArray.fromObject(res);
            //System.out.println(res.substring(res.indexOf("[")+1,res.indexOf("]")));
            try {
                int i=Integer.parseInt(res.substring(res.indexOf("[")+1,res.indexOf("]")));
                return i>0;
            } catch (Exception e) {
                return false;
            }


        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet executeQuery(String sql) {
        try {
            String res;
            sql += "\n";
            out.write(sql.getBytes());//传输给服务器
            res = reader.readLine();
            //System.out.println(res);
            JSONArray json = JSONArray.fromObject(res);
            List<Record> list = (List<Record>) JSONArray.toList(json, Record.class);
            ResultSet rs = new ResultSet(list);
            return rs;
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ResultSet(null);
    }

}
