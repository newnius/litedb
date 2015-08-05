package com.newnius.litedb.c;

import com.newnius.litedb.m.FileManager;
import com.newnius.litedb.m.Record;
import com.newnius.litedb.m.Table;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Newnius
 */
public class MemoryManager {

    private static final Map<String, Table> tables = new HashMap<>();
    private static final FileManager fileManager = new FileManager();

    public MemoryManager() {
        init();
    }

    private void init() {
        File dir = new File("db");
        File[] fs = dir.listFiles();
        for (File f : fs) {
            if (!f.isDirectory()) {
                String fileName = f.getName();
                if (fileName.endsWith(".tbl")) {
                    String tableName = fileName.substring(0, fileName.indexOf("."));
                    tables.put(tableName, getTableConfig(tableName));
                }
            }
        }

        Collection<Table> c = tables.values();
        Iterator it = c.iterator();
//        for (; it.hasNext();) {
//            System.out.println(it.next());
//        }
    }

    public Table getTableConfig(String tableName) {
        String tbl = fileManager.readTableConfig(tableName);
        try {
            String[] tmp = tbl.split("\\*\\*");
            String[] cnts = tmp[0].split("\\*");
            int i = 0;
            int pKeysCnt = Integer.parseInt(cnts[0]);
            int keysCnt = Integer.parseInt(cnts[1]);
            int indexsCnt = Integer.parseInt(cnts[2]);
            String[] keysName = new String[keysCnt];
            String[] keysType = new String[keysCnt];
            String[] keysLength = new String[keysCnt];
            String[] indexs = new String[indexsCnt];
            String pKey = tmp[1].replaceAll("\\*", "");
            for (i = 2; i < keysCnt + 2; i++) {
                String[] keys = tmp[i].split("\\*");
                keysName[i - 2] = keys[0];
                keysType[i - 2] = keys[1];
                keysLength[i - 2] = keys[2];
            }
            int cnt = 0;
            for (; i < indexsCnt + pKeysCnt + 2; i++) {
                indexs[cnt++] = tmp[i].replaceAll("\\*", "");
            }

            return new Table(tableName, pKey, keysName, keysType, keysLength, indexs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List insert(String table, String[] keys, String[] values) {
//        System.out.println("checking queries");
//        System.out.println(table);
//        for (int i = 0; i < keys.length; i++) {
//            System.out.println(keys[i] + "=" + values[i]);
//        }
//        System.out.println("checking finished");
//        if (1 == 1) {
//            return null;
//        }

        List res = new ArrayList();
        if (tables.containsKey(table)) {
            synchronized (tables.get(table)) {
                res.add(tables.get(table).insert(keys, values));
            }
        }else{
            res.add(0);
        }
        return res;
    }

    public List<Record> select(String table, String[] keys, String whereClause, String orderBy, boolean desc, int offset, int rows) {
//        System.out.println("checking queries");
//        System.out.println(table);
//        for (int i = 0; i < keys.length; i++) {
//            System.out.println(keys[i]);
//        }
//        for (int i = 0; i < whereClause.length; i++) {
//            System.out.println(whereClause[i]);
//        }
//        if (orderBy != null) {
//            System.out.println(orderBy);
//        }
//        System.out.println(desc);
//        System.out.println("offset=" + offset + " rows=" + rows);
//        System.out.println("checking finished");
//
//        if (1 == 1) {
//            return null;
//        }
        List<Record> res = new ArrayList();
        if (tables.containsKey(table)) {
            synchronized (tables.get(table)) {
                res = tables.get(table).select(keys, whereClause, orderBy, desc, offset, rows);
            }
        }
        return res;
    }

    public List delete(String table, String whereClause) {

//        System.out.println("checking queries");
//        System.out.println(table);
//        for (int i = 0; i < whereClause.length; i++) {
//            System.out.println(whereClause[i]);
//        }
//        System.out.println("checking finished");
//        if (1 == 1) {
//            return null;
//        }
        List res = new ArrayList();
        if (tables.containsKey(table)) {
            synchronized (tables.get(table)) {
                res.add(tables.get(table).delete(whereClause));
            }
        }else{
            res.add(0);
        }
        return res;
    }

    public List update(String table, String[] keys, String[] values, String whereClause) {
//        System.out.println("checking queries");
//        System.out.println(table);
//        for (int i = 0; i < keys.length; i++) {
//            System.out.println(keys[i] + "=" + values[i]);
//        }
//
//        for (int i = 0; i < whereClause.length; i++) {
//            System.out.println(whereClause[i]);
//        }
//        // System.out.println("offset=" + offset + " rows=" + rows);
//        System.out.println("checking finished");
//
//        if (1 == 1) {
//            return null;
//        }
        List res = new ArrayList();
        if (tables.containsKey(table)) {
            synchronized (tables.get(table)) {
                res.add(tables.get(table).update(keys, values, whereClause));
            }
        }else{
            res.add(0);
        }

        return res;
    }

    public List create(String table, String[] key_names, String[] key_types, int[] key_lengths, String primary_key, String[] indexs) {

//        System.out.println("checking queries");
//        System.out.println(table);
//        for (int i = 0; i < key_names.length; i++) {
//            System.out.println(key_names[i] + " " + key_types + " " + key_lengths);
//        }
//        System.out.println(primary_key);
//        for (int i = 0; indexs!=null&&i < indexs.length; i++) {
//            System.out.println(indexs[i]);
//        }
//        System.out.println("checking finished");
//        if (1 == 1) {
//            return null;
//        }
        List res = new ArrayList();
        if (FileManager.create(table, key_names, key_types, key_lengths, primary_key, indexs) > 0) {
            tables.put(table, getTableConfig(table));
            res.add(1);
        }else{
            res.add(0);
        }
        return res;
    }

    public List drop(String table) {

//        System.out.println("checking queries");
//        System.out.println(table);
//        System.out.println("checking finished");
//        if (1 == 1) {
//            return null;
//        }
        List res = new ArrayList();
        if (tables.containsKey(table)) {
            synchronized (tables.get(table)) {
                tables.remove(table);
                res.add(FileManager.drop(table));
            }
        }else{
            res.add(0);
        }
        return res;
    }

}
