package com.newnius.litedb.m;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Newnius
 */
public class FileManager {

    public static String readTableConfig(String table) {
        String encoding = "utf-8";
        File tblFile = new File("db", table + ".tbl");
        Long fileLength = tblFile.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(tblFile);
            in.read(fileContent);
            in.close();
            return new String(fileContent, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static String readTableData(String table) {
        String encoding = "utf-8";
        File tblFile = new File("db", table + ".data");
        Long fileLength = tblFile.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(tblFile);
            in.read(fileContent);
            in.close();
            return new String(fileContent, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public static int create(String table, String[] key_names, String[] key_types, int[] key_lengths, String primary_key, String[] indexs) {
        try {
            File tblFile = new File("db", table + ".tbl");
            File dataFile = new File("db", table + ".data");
            if (tblFile.exists()) {
                //System.out.println("exist");
                return 0;
            } else {
                /**
                 * create .tbl and .data
                 */
                try {
                    tblFile.createNewFile();
                    dataFile.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tblFile)));
                    String tblStr = null;
                    int m = key_names != null ? key_names.length : 0;
                    int n = indexs != null ? indexs.length : 0;
                    //1 primary_key, m keys, n indexs;
                    tblStr = "1*" + m + "*" + n + "**";
                    tblStr += primary_key + "**";
                    for (int i = 0; i < key_names.length; i++) {
                        tblStr += key_names[i] + "*" + key_types[i] + "*" + key_lengths[i] + "**";
                    }
                    for (int i = 0; i < n; i++) {
                        tblStr += indexs[i] + "**";
                    }
                    bw.write(tblStr);
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static int drop(String table) {
        try {
            File tblFile = new File("db", table + ".tbl");
            File dataFile = new File("db", table + ".data");
            if (tblFile.exists()) {
                tblFile.delete();
            }
            if (dataFile.exists()) {
                dataFile.delete();
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
