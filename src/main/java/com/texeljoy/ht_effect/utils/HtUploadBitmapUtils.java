package com.texeljoy.ht_effect.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName HtUploadBitmapUtils
 * @Description TODO 自定义上传图片
 * @Author lu guaiguai
 * @Date 2023/8/1 9:56
 */
public class HtUploadBitmapUtils {


    //对于获取用户图片选中的后处理
    public static String handleImageBeforeKitKat(Intent data, Activity activity) {
        Uri uri = data.getData();
        if (uri == null) return null;
        String imagePath = getImagePath(uri, null, activity);
        if (!isPng(imagePath)) {
            Toast.makeText(activity.getBaseContext(), "暂时只兼容png格式的背景图", Toast.LENGTH_SHORT).show();
            return null;
        }
        return imagePath;


    }

    /**
     * @param uri 传过来的uri
     * @param selection 规则
     * @return 路径
     */
    private static String getImagePath(@NonNull Uri uri, @Nullable String selection, @Nullable Activity activity) {
        String path = null;
        Cursor cursor = activity.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 用于判断格式是否正确
     *
     * @param path 路径
     * @return 结果
     */
    private static boolean isPng(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        Log.i("添加的绿幕的背景图格式：", options.outMimeType);
        return "image/png".equals(type);
    }
}
