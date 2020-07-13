package com.union_test.toutiao.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TempFileUtils {
    private FileWriter fw;



    public void writeToFile(String fileName,String content){
        //读取本地缓存文件
        try {
            //String rootPath =  Environment.getExternalStorageDirectory().getPath();
            //File file = new File(rootPath + "/Android/data/com.snssdk.api/cache/"+filename);
            String packageName = "com.union_test.internationad";
            File file = new File(  "data/data/"+packageName+"/");
            if (!file.exists()) {
                file.mkdir();
            }
            fileName = file.getAbsolutePath()+"/"+fileName;
            file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            fw = new FileWriter(file, false);
            fw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fw!=null){
                    fw.flush();
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFile(String fileName){
        try {
            String packageName = "com.union_test.internationad";
            File file = new File(  "data/data/"+packageName+"/"+fileName);
            if (file.exists()) {
                file.delete();
            }
            return;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
