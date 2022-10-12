package com.gesturesuite.apkplugin;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Created by basilis on 5/14/2018.
 */

public class PluginFileProvider extends FileProvider {

    public static Uri getUriFromFile(Context context, File file){
        try {
            return getUriForFile(context, context.getString(R.string.plugin_file_provider), file);
        }catch(Exception e){
            return null;
        }
    }

    public static Uri getUriFromFile(Context context, String filePath){
        try{
            return getUriForFile(context, context.getString(R.string.plugin_file_provider), new File(filePath));
        }catch(Exception e){
            return null;
        }
    }

}
