package com.newnius.litedbDriver;

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

    
    public String get(int i) {
        return values[i];
    }
    
    public int size(){
        return values.length;
    }

}
