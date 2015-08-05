package com.newnius.litedbDriver;

import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Newnius
 */
public class Test {

    public static void main(String[] args) {
        Connection conn = new Connection();
        ResultSet rs;
        rs = conn.executeQuery("select key_0,key_1 from test where 1==1 and key_0!=\"vivian\" limit 1,2");
        for (int i = 0; i < rs.size(); i++) {
            for (int j = 0; j < rs.get(i).size(); j++) {
                System.out.print(rs.get(i).get(j) + " ");
            }
            System.out.println();
        }

        rs = conn.executeQuery("select key_0,key_1,key_2 from test where 1==1 and key_0!=\"vivian\" limit 2");
        for (int i = 0; i < rs.size(); i++) {
            for (int j = 0; j < rs.get(i).size(); j++) {
                System.out.print(rs.get(i).get(j) + " ");
            }
            System.out.println();
        }

        rs = conn.executeQuery("select key_0,key_1 from test where 1==1 and key_0!=\"vivian\" order by key_0 desc limit 0,31");
        for (int i = 0; i < rs.size(); i++) {
            for (int j = 0; j < rs.get(i).size(); j++) {
                System.out.print(rs.get(i).get(j) + " ");
            }
            System.out.println();
        }

        //System.out.println(conn.execute("select key_0,key_1 from test where 1==1 and key_0!=\"vivian\" limit 1,-1"));
        System.out.println(conn.execute("insert into test (key_0,key_1,key_2) values(vivian,sz,13800000000)"));
        //System.out.println(conn.execute("delete from test where 1==1 and key_0==\"vivian\""));
        System.out.println(conn.execute("select key_0,key_1 from test where 1==1 and key_0!=\"vivian\" limit 1,-1"));
        System.out.println(conn.execute("select key_0,key_1 from test where 1==1 and key_0!=\"vivian\" limit 1,-1"));

    }
}
