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
            Socket client = new Socket("192.168.40.1", 7096);//�����ͻ����׽���
            out = client.getOutputStream();//��ȡ�����
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));//��ȡ������ ��÷��������ص�����
            localMessage = new BufferedReader(new InputStreamReader(System.in));//���ܿͻ��˴Ӽ����������Ϣ
            System.out.println(reader.readLine());
        } catch (IOException e) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean execute(String sql) {
        try {
            String res;
            sql += "\n";
            out.write(sql.getBytes());//�����������
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
            out.write(sql.getBytes());//�����������
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
