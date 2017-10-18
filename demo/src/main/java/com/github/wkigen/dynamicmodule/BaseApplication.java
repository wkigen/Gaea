package com.github.wkigen.dynamicmodule;

import android.app.Application;

import com.github.wkigen.gaea.GaeaSystem;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Created by vicky on 2017/10/18.
 */
public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        GaeaSystem.getInstance().init(this);

        loadBundle();
    }

    private void loadBundle(){
        try {
            ZipFile zipFile = new ZipFile(getApplicationInfo().sourceDir);
            processBundle(zipFile,"assets/hello.apk");
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }catch (Exception e){
        }
    }

    public void processBundle(ZipFile zipFile, String path){
        try {
            String[] temp = path.split("//");
            GaeaSystem.getInstance().installBundle(temp[temp.length - 1], zipFile.getInputStream(zipFile.getEntry(path)));
        }catch (Exception E){

        }

    }

}
