package com.newnius.litedb.m;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Newnius
 */
public class Table {

    private final String tableName;
    private final String privateKey;
    private final String[] keyNames;
    private final String[] keyTypes;
    private final String[] keyLengths;
    private final String[] indexs;
    private boolean upToDate;
    private int keysCnt = 0;
    private int indexsCnt = 0;
    List<Record> records = new ArrayList<>();
    Map<String, Integer> keysMap;
    public static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

    public Table(final String tableName, String privateKey, String[] keyNames, String[] keyTypes, String[] keyLengths, String[] indexs) {
        this.keysMap = new HashMap<>();
        this.tableName = tableName;
        this.privateKey = privateKey;
        this.keyNames = keyNames;
        this.keyTypes = keyTypes;
        this.keyLengths = keyLengths;
        this.indexs = indexs;
        upToDate = true;
        if (keyNames != null) {
            keysCnt = keyNames.length;
        }
        if (indexs != null) {
            indexsCnt = indexs.length;
        }

        for (int i = 0; i < keysCnt; i++) {
            keysMap.put(keyNames[i], i);
        }

        loadRecords();
        checkRecords();

        new Thread() {
            @Override
            public void run() {
                while (com.newnius.litedb.c.Global.isDBPrepared) {
                    writeBack();
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }.start();
    }

    private int getKeyIndex(String keyName) {
        System.out.println(keyName);
        return keysMap.get(keyName);
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String[] getKeyNames() {
        return keyNames;
    }

    public String[] getKeyTypes() {
        return keyTypes;
    }

    public String[] getKeyLengths() {
        return keyLengths;
    }

    public String[] getIndexs() {
        return indexs;
    }

    public int getKeysCnt() {
        return keysCnt;
    }

    public int getIndexsCnt() {
        return indexsCnt;
    }

    public int getSum() {
        return records.size();
    }

    @Override
    public String toString() {
        String str = "";
        str += this.tableName + " ";
        str += this.privateKey + " ";
        for (int i = 0; i < keysCnt; i++) {
            str += keyNames[i] + " " + keyTypes[i] + " " + keyLengths[i] + " ";
        }
        for (int i = 0; i < indexsCnt; i++) {
            str += indexs[i];
        }
        return str;
    }

    public void loadRecords() {
        String data = FileManager.readTableData(tableName);
        try {
            String[] tmp = data.split("\\*\\*");
            String[] values;
            for (int i = 0; i < tmp.length && tmp[i].length() != 0; i++) {
                values = tmp[i].split("\\*");
                records.add(new Record(values));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int insert(String[] keys, String[] values) {
        //consider *
        checkRecords();
        String[] values2 = new String[keysCnt];
        for (int i = 0; i < keysCnt; i++) {
            values2[i] = "";
            for (int j = 0; j < keys.length; j++) {
                if (keys[j].equals(keyNames[i])) {
                    values2[i] = values[i];
                    break;
                }
            }
        }
        synchronized (records) {
            records.add(new Record(values2));
        }

        upToDate = false;
        checkRecords();
        return 1;
    }

    public List select(String[] keys, String whereClause, String orderBy, boolean desc, int offset, int rows) {
        List<Record> rs = selectAll(whereClause, orderBy, desc, offset, rows);
//        JSONArray jsonArray=JSONArray.fromObject(rs);
//        System.out.println(jsonArray);
        List<Record> newrs=new ArrayList();
        
        for(Record record:rs){
            String[] values=new String[keys.length];
            for(int i=0;i<keys.length;i++){
                values[i]=record.get(getKeyIndex(keys[i]));
            }
            newrs.add(new Record(values));
        }
        System.out.println(keys[0]);
        if(keys[0].equals("\\*")){
            return rs;
        }
        
        return newrs;
    }

    public List selectAll(String whereClause, String orderBy, boolean desc, int offset, int rows) {
        List<Record> rs = new ArrayList<>();
        try {
            if (whereClause == null || whereClause.length() < 1) {
                synchronized (records) {
                    for (Record tmp : records) {
                        if (offset-- > 0) {
                            continue;//sth
                        }
                        if (rows-- == 0) {
                            break;
                        }
                        rs.add(tmp);

                    }
                }

            } else {

                for (Record tmp : records) {
                    if (offset-- > 0) {
                        continue;//sth
                    }
                    if (rows-- == 0) {
                        break;
                    }                    
                    if (handleWhere(whereClause,tmp)) {
                        rs.add(tmp);
                    } else {
                        continue;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ListSortUtil<Record> sortList = new ListSortUtil<Record>();
        if(orderBy==null)orderBy=this.privateKey;
        sortList.sort(rs, getKeyIndex(orderBy), desc);
        return rs;
    }

    public int delete(String whereClause) {
        List<Record> rs = selectAll(whereClause, null, false, 0, -1);
        synchronized (records) {
            records.removeAll(rs);
        }

        upToDate = false;
        return rs == null ? 0 : rs.size();
    }

    public int update(String[] keys, String[] values, String whereClause) {
        List<Record> rs = selectAll(whereClause, null, false, 0, -1);
        for (Record record : rs) {
            for (int i = 0; i < keys.length; i++) {
                record.set(getKeyIndex(keys[i]), values[i]);
            }
        }
        upToDate = false;
        return rs == null ? 0 : rs.size();
    }

    private void checkRecords() {
//        System.out.println("checking records in table " + tableName);
//        for (Record tmp : records) {
//            System.out.println(tmp);
//        }
//        System.out.println("total is "+records.size());
//        System.out.println("checking finished");
    }

    private void indexTree() {//balanced tree

    }

    private boolean handleWhere(String whereClause,Record record) {
        String query = whereClause;
        System.out.println(query);
        query = query.replaceAll("and", "&&").replaceAll("or", "||");
        query = replace(query,record);
        long s = System.currentTimeMillis();
        try {
            String res = scriptEngine.eval(query) + "";
            return res.equals("true");
        } catch (ScriptException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }

        long e = System.currentTimeMillis();
        System.out.println(e - s);
        return true;
    }

    public void writeBack() {
        if (!upToDate) {
            try {
                synchronized (this) {
                    File dataFile = new File("db", tableName + ".data");
                    if (!dataFile.exists()) {
                        return;
                    }
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile)));
                    String str = "";
                    for (Record record : records) {
                        String[] tmp = record.getValues();
                        for (int i = 0; i < tmp.length; i++) {
                            str += tmp[i] + "*";
                        }
                        str += "*";
                    }

                    bw.write(str);
                    bw.close();
                }
                System.out.println("ddsadsadsadsadsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasa");
            } catch (Exception e) {
                e.printStackTrace();
            }
            upToDate = true;
        }
    }

    public  String replace(String str,Record record) {
        String regex = "(?:`(?:\\s*(?<key>[a-z][_a-z0-9]+)\\s*`)|(\\s*(?<key1>[a-z][_a-z0-9]+)\\b(?![\"'].*)\\s*))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            if (m.group() != null) {
                //先将前面的字符串加上，然后加上此次的字符串
                System.out.println("m.group():"+m.group());
                m.appendReplacement(sb, "\""+record.get(this.getKeyIndex(m.group("key")==null?m.group("key1"):m.group("key")))+"\"");
                //System.out.println(m.group());
            }
        }
        //这个表示将剩下的字符加到尾部
        m.appendTail(sb);
        //System.out.println(sb);
        System.out.println("sb:\""+sb.toString()+"\"");
        return sb.toString();
    }

}

class ListSortUtil<T> {

    /**
     * @param targetList 目标排序List
     * @param sortField 排序字段(实体类属性名)
     * @param desc 排序方式（true or false）
     */
    //@SuppressWarnings({"unchecked", "rawtypes"})
    public void sort(List<T> targetList, final int index, final boolean desc) {

        Collections.sort(targetList, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                int retVal = 0;
                try {
                    //首字母转大写  
                    //String newStr = sortField.substring(0, 1).toUpperCase() + sortField.replaceFirst("\\w", "");
                    String methodStr = "get";

                    Method method1 = ((T) obj1).getClass().getMethod(methodStr, int.class);
                    Method method2 = ((T) obj2).getClass().getMethod(methodStr, int.class);
                    if (desc) {
                        retVal = method2.invoke(((T) obj2), index).toString().compareTo(method1.invoke(((T) obj1), index).toString()); // 倒序  
                    } else {
                        retVal = method1.invoke(((T) obj1), index).toString().compareTo(method2.invoke(((T) obj2), index).toString()); // 正序  
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return retVal;
            }
        });
    }

}
