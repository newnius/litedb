package com.newnius.litedb.v;

import com.newnius.litedb.c.RemoteServer;
import com.newnius.litedb.c.Global;
import com.newnius.litedb.c.sqlParser;
import java.util.List;
import java.util.Scanner;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Newnius
 */
public class test {

    public static void main(String[] args) {

        Global.isDBPrepared = true;
        Global.isRemoteAccessAvailable = true;

        new RemoteServer().start();

        Scanner scan = new Scanner(System.in);
        while (true) {
            String sql = "";
            while (scan.hasNext()) {
                sql += " "+scan.next();
                if (sql.endsWith(";")) {
                    break;
                }
            }

            if (sql.equals(" shutdown;")) {
                System.exit(0);
            }
            System.out.println(sql);
            List list=sqlParser.parse(sql);
            for(Object record:list){
                System.out.println(record);
            }

        }

    }
}
