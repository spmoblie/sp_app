package com.spshop.stylistpark.task;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.db.DecorationDBManager;
import com.spshop.stylistpark.utils.Decompress;
import com.spshop.stylistpark.utils.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadDbThread extends Thread
{
    private static final String TAG = "DownloadDbThread";
    
    public static final int FAIL = 0;
    public static final int SUCCESS = 1;
    
    private Context ctx;
    private Handler mHandler;
    
    public DownloadDbThread(Context ctx, Handler mHandler)
    {
        this.ctx = ctx;
        this.mHandler = mHandler;
    }

    public void run()
    {
        Message msg = new Message();
        int dlResult = FileManager.downloadFile(AppConfig.URL_DECOR_DB, AppConfig.NAME_DECOR_ZIP);
        if(dlResult != FileManager.SUCCESS)
        {
            msg.what = FAIL;
        } else {
            String zipFile = ctx.getFilesDir() + "/" + AppConfig.NAME_DECOR_ZIP; 
            String unzipLocation = ctx.getDatabasePath(AppConfig.NAME_DECOR_TEMP_DB).getParentFile().getAbsolutePath();
//            String unzipLocation = ctx.getFilesDir().getAbsolutePath();
            Decompress d = new Decompress(zipFile, unzipLocation, AppConfig.NAME_DECOR_TEMP_DB); 
            d.unzip();
            File file = new File(unzipLocation, AppConfig.NAME_DECOR_TEMP_DB);
            Log.d(TAG, "run file exist: " + file.exists());
            // check db available
            if(!DecorationDBManager.getInstance(ctx).getAllDecorationFromTemp().isEmpty()) {
                InputStream in = null;
                OutputStream out = null;
                try
                {
                    in = new FileInputStream(unzipLocation+"/"+AppConfig.NAME_DECOR_TEMP_DB);
                
                    File outFile = new File(ctx.getDatabasePath(AppConfig.NAME_DECOR_ZIP).getParentFile().getAbsolutePath(), DecorationDBManager.DB_NAME);
                    out = new FileOutputStream(outFile);
                } catch (FileNotFoundException e)
                {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                try
                {
                    copyFile(in, out);
                } catch (IOException e)
                {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                
                msg.what = SUCCESS;
            } else {
                msg.what = FAIL;
            }
        }
        
        if (!interrupted())
        {
            mHandler.sendMessage(msg);
        }
    }
    
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
}
