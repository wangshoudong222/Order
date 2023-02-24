package com.yun.orderPad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.yun.orderPad.BaseContext;
import com.yun.orderPad.OrderApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FileUtil {
  private static final String TAG = "FileUtil";

  private static final String DIR_APK = "";
  private static final String DIR_LOG = "order/";

  private static final String DIR_ROOT = "order/";

  public static File getAppDir() {
    return getDir(DIR_ROOT);
  }

  public static File getApkDir() {
    return getDir(DIR_APK);
  }

  public static File getLogDir() {
    return getDir(DIR_LOG);
  }

  /**
   * 获取存储目录
   */
  private static File getDir(String name) {
    File file;
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      file = new File(Environment.getExternalStorageDirectory(), name);
    } else {
      file = new File(BaseContext.Companion.getInstance().getContext().getFilesDir(), name);
    }

    if (!file.exists()) {
      boolean b = file.mkdirs();
      if (!b) {
        file.setReadable(true);
        file.setWritable(true);
      }
    }

    return file;
  }

  /**
   * 获取本地文件, url是应图片地址
   */
  public static File getUrlFile(File dir, String url) {
    if (TextUtils.isEmpty(url)) return null;
    String targetName = getFileName(url);
    return new File(dir, targetName);
  }

  private static String getFileName(String url) {
    String[] urlSplit = url.split("/");
    String fileName = urlSplit[urlSplit.length - 1];
    urlSplit = fileName.split("!");
    fileName = urlSplit[0];
    return fileName;
  }

  /**
   * 删除文件，可以是文件或文件夹
   */
  public static boolean delete(File file) {
    if (file == null || !file.exists()) {
      return false;
    }

    if (file.isFile()) {
      return deleteFile(file);
    } else {
      return deleteDirectory(file);
    }
  }

  /**
   * 删除单个文件
   */
  private static boolean deleteFile(File file) {
    if (file.exists() && file.isFile()) {
      return file.delete();
    }
    return false;
  }

  /**
   * 删除目录及目录下的文件
   *
   * @param dir 要删除的目录的文件路径
   * @return 目录删除成功返回true，否则返回false
   */
  private static boolean deleteDirectory(File dir) {
    // 如果dir对应的文件不存在，或者不是一个目录，则退出
    if (!dir.exists() || !dir.isDirectory()) {
      LogUtil.d(TAG, "删除目录失败：" + dir + "不存在！");
      return false;
    }

    boolean flag = true;
    // 删除文件夹中的所有文件包括子目录
    File[] files = dir.listFiles();
    for (int i = 0; files != null && i < files.length; i++) {
      // 删除子文件
      if (files[i].isFile()) {
        flag = deleteFile(files[i]);
        LogUtil.d(TAG, "删除文件" + files[i] + "成功！");
        if (!flag) break;
      }

      // 删除子目录
      else if (files[i].isDirectory()) {
        flag = deleteDirectory(files[i]);
        if (!flag) break;
      }
    }

    if (!flag) {
      LogUtil.d(TAG, "删除目录失败！");
      return false;
    }

    // 删除当前目录
    if (dir.delete()) {
      LogUtil.d(TAG, "删除目录" + dir + "成功！");
      return true;
    } else {
      return false;
    }
  }
}
