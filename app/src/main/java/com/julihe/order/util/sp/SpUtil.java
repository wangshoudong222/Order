package com.julihe.order.util.sp;

import com.julihe.order.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class SpUtil {
  private static final String TAG = "SpUtil";
  private static Map<String, Object> cacheMap = new HashMap<>();

  private static <T> T saveAndGet(String key, Class<T> clazz, T[] val) {
    SPManager sp = SPManager.getInstance();

    //有值视为set
    if (val == null) {//null值视为删除
      boolean suc = sp.delete(key);
      if (suc) cacheMap.remove(key);
      return null;
    } else if (val.length > 0) {
      boolean suc = sp.save(key, val[0]);
      if (suc) cacheMap.put(key, val[0]);
      return val[0];
    }

    //无值视为get
    T o = (T) cacheMap.get(key);
    if (o != null) {
      LogUtil.i(TAG, "read from cache, key:" + key);
      return o;
    }

    return sp.getObject(key, clazz);
  }

  public static String deviceId(String... id) {
    return saveAndGet("device_id", String.class, id);
  }

  public static String token(String... token) {
    String tokenString =  saveAndGet("token", String.class, token);
    return tokenString;
  }

  public static String config(String... config) {
    return saveAndGet("config", String.class, config);
  }

  public static boolean cleanAll() {
    cacheMap.clear();
    return SPManager.getInstance().clearAll();
  }
}