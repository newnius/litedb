package com.newnius.litedb.m;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Newnius
 */
public class Record{

    private  String[] values;

    public void setValues(String[] values) {
        this.values = values;
    }

    public Record(String[] values) {
        this.values = values;
    }

    public Record() {
    }
    

    @Override
    public String toString() {
        String str = "";
        for (String tmp : values) {
            str += tmp + " ";
        }
        return str;
    }

    public String[] getValues() {
        return values;
    }
    
    public String get(int i) {
        return values[i];
    }
    
    public boolean set(int index,String value){
        values[index]=value;
        return true;
    }


}
