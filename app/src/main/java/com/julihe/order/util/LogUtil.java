package com.julihe.order.util;

import android.text.TextUtils;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.julihe.order.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 本地打印日志
 */
public class LogUtil {
  public static final String TAG_GLOBAL = "OrderLog";

  private static final long MAX_TIME = 1000 * 60 * 60 * 24 * 2; // 最多打印两天日志

  static {
    int level = BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.DEBUG;
    LogConfiguration config = new LogConfiguration.Builder().logLevel(level).build();

    AndroidPrinter androidPrinter = new AndroidPrinter();
    Printer filePrinter = new FilePrinter// 打印日志到文件的打印器
        .Builder(FileUtil.getLogDir().getAbsolutePath())// 指定保存日志文件的路径
        .fileNameGenerator(new DateFileNameGenerator())// 指定日志文件名生成器
        .backupStrategy(new NeverBackupStrategy())// 指定日志文件备份策略，默认为FileSizeBackupStrategy(1024 * 1024)
        .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))// 指定日志文件清除策略，默认为 NeverCleanStrategy()
        .build();

    XLog.init(config,androidPrinter,filePrinter);
  }

  public static File getLogFile(long time){
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    return new File(FileUtil.getLogDir(), format.format(time));
  }

  public static void i(String msg) {
    i(null, msg);
  }

  public static void i(String tag, String msg) {
    tag = TextUtils.isEmpty(tag) ? TAG_GLOBAL : TAG_GLOBAL + "|" + tag;
    tag = joinTime(tag);
    XLog.tag(tag).i(msg);
  }

  public static void d(String msg) {
    d(null, msg);
  }

  public static void d(String tag, String msg) {
    tag = TextUtils.isEmpty(tag) ? TAG_GLOBAL : TAG_GLOBAL + "|" + tag;
    tag = joinTime(tag);
    XLog.tag(tag).d(msg);
  }

  public static void w(String msg) {
    w(null, msg);
  }

  public static void w(String tag, String msg) {
    tag = TextUtils.isEmpty(tag) ? TAG_GLOBAL : TAG_GLOBAL + "|" + tag;
    tag = joinTime(tag);
    XLog.tag(tag).w(msg);
  }

  public static void e(String msg) {
    e(null, msg);
  }

  public static void e(String tag, String msg) {
    e(tag, msg, null);
  }

  public static void e(String msg, Throwable tr) {
    e(null, msg, tr);
  }

  public static void e(String tag, String msg, Throwable tr) {
    tag = TextUtils.isEmpty(tag) ? TAG_GLOBAL : TAG_GLOBAL + "|" + tag;
    tag = joinTime(tag);
    XLog.tag(tag).e(msg, tr);
  }

  public static String joinTime(String msg) {
    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
    return format.format(System.currentTimeMillis()) + "|" + msg;
  }

  public interface LogZipInterface {
    void onSuccess(File file);
  }
}
