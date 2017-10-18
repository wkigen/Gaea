package com.github.wkigen.gaea;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicky on 2017/10/18.
 */
public class GaeaSystem {

    private static GaeaSystem mInstance;

    private List<Bundle> mBundles;

    private GaeaSystem(){
        mBundles = new ArrayList<Bundle>();
    }

    public static GaeaSystem getInstance(){
        if (mInstance == null){
            synchronized (GaeaSystem.class){
                if (mInstance == null){
                    mInstance = new GaeaSystem();
                }
            }
        }
        return mInstance;
    }

    public void init(Application application){
        RuntimeArgs.mainApplication = application;
    }

    public void installBundle(String name,InputStream inputStream){
        try{
            File dexDir = new File(RuntimeArgs.mainApplication.getApplicationInfo().dataDir);
            File dexFile = new File(dexDir,"bundle.apk");
            FileUtils.copyInputStreamToFile(inputStream,dexFile);
            ArrayList<File> files = new ArrayList<>();
            files.add(dexFile);
            BundleLoader.installBundleDex(RuntimeArgs.mainApplication.getClassLoader(),dexDir,files);
        }catch (Exception e){
            Log.e("installBundle",e.getMessage());
        }

    }
}
