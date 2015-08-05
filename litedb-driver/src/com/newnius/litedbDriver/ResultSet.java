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
public class ResultSet{
    
    private int offset=-1;

    List<Record> records;
    ResultSet(List<Record> list) {
        records=list;
    }
    
    public Record get(int i) {
        return records.get(i);    
    }
    
    public int size(){
        return records.size();
    }
    
    
    public boolean next(){
        if(++offset<records.size()){
            return true;
        }else{
            return false;
        }
    }

}
