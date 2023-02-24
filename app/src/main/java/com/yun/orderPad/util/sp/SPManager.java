package com.yun.orderPad.util.sp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.yun.orderPad.BaseContext;
import com.yun.orderPad.OrderApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 1.数据只能被本应用程序读、写 context.MODE_PRIVATE
 * 2.新内容追加到原内容后 Context.MODE_APPEND
 * 3.能被其他应用程序读，但是不支持写Context.MODE_WORLD_READABLE
 * 4.能被其他应用程序读、写会覆盖原数据Context.MODE_WORLD_WRITEABLE
 */
public class SPManager {
  private static Map<String, SPManager> sSpManagerMap = new HashMap<>();

  private SharedPreferences mSp;

  /**
   * GSON转换类
   */
  private Gson mGson = new Gson();

  private SPManager() {
  }

  public static SPManager getInstance() {
    return getInstance("config");
  }

  public static SPManager getInstance(String spName) {
    SPManager spManager = sSpManagerMap.get(spName);
    if (spManager == null) {
      synchronized (SPManager.class) {
        spManager = sSpManagerMap.get(spName);
        if (spManager == null) {
          spManager = new SPManager();
          spManager.mSp = BaseContext.Companion.getInstance().getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
          sSpManagerMap.put(spName, spManager);
        }
      }
    }
    return spManager;
  }

  public boolean save(String key, String value) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putString(key, value);
    return editor.commit();
  }

  public String getString(String key, String value) {
    return mSp.getString(key, "");
  }

  public boolean save(String key, int var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putInt(key, var);
    return editor.commit();
  }

  public int getInt(String key, int var) {
    if (var != 0) {
      return mSp.getInt(key, var);
    } else {
      return mSp.getInt(key, 0);
    }
  }

  public boolean save(String key, long var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putLong(key, var);
    return editor.commit();
  }

  public long getLong(String key) {
    return mSp.getLong(key, 0L);
  }

  public boolean save(String key, float var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putFloat(key, var);
    return editor.commit();
  }

  public float getFloat(String key) {
    return mSp.getFloat(key, 0f);
  }

  public boolean getBoolean(String key, boolean defValue) {
    return mSp.getBoolean(key, defValue);
  }

  public boolean save(String key, boolean defValue) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putBoolean(key, defValue);
    return editor.commit();
  }

  /**
   * 存储对象
   */
  public boolean save(String key, Object var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putString(key, mGson.toJson(var));
    return editor.commit();
  }

  /**
   * get对象
   */
  public <T> T getObject(String key, Class<T> clazz) {
    String obj = mSp.getString(key, null);
    if (obj != null) {
      try {
        return mGson.fromJson(obj, clazz);
      } catch (JsonSyntaxException e) {
        e.printStackTrace();
        //解析错误时全部清除
        clearAll();
      }
    }
    return null;
  }

  /**
   * 删除sp某key
   */
  public boolean delete(String key) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.remove(key);
    return editor.commit();
  }

  /**
   * 清空
   */
  public boolean clearAll() {
    SharedPreferences.Editor editor = mSp.edit();
    editor.clear();
    return editor.commit();
  }

  /////////////////////////////////////////////////

  public void asyncSave(String key, String value) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putString(key, value);
    editor.apply();
  }

  public void asyncSave(String key, long var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putLong(key, var);
    editor.apply();
  }

  public void asyncSave(String key, float var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putFloat(key, var);
    editor.apply();
  }

  public void asyncSave(String key, boolean defValue) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putBoolean(key, defValue);
    editor.apply();
  }

  /**
   * 存储对象
   */
  public void asyncSave(String key, Object var) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.putString(key, mGson.toJson(var));
    editor.apply();
  }

  /**
   * 删除sp某key
   */
  public void asyncDelete(String key) {
    SharedPreferences.Editor editor = mSp.edit();
    editor.remove(key).apply();
  }

  /////////////////////////////////////////////////

  /**
   * @param key sp的key
   * @param cls list中的moduel类型
   */
  public <T> List<T> getList(String key, Class<T> cls) {
    List<T> lst = new ArrayList<>();

    String result = mSp.getString(key, null);
    if (result != null) {
      JsonArray array = new JsonParser().parse(result).getAsJsonArray();
      for (final JsonElement elem : array) {
        lst.add(mGson.fromJson(elem, cls));
      }
    }
    return lst;
  }

  /**
   * @param key sp的key
   */
  public <T> Set<T> getSet(String key, Class<T> cls) {
    Set<T> set = new HashSet();

    String result = mSp.getString(key, null);
    if (result != null) {
      JsonArray array = new JsonParser().parse(result).getAsJsonArray();
      for (final JsonElement elem : array) {
        set.add(mGson.fromJson(elem, cls));
      }
    }
    return set;
  }

  /**
   * @param key sp中key
   * @param valueCls map中value类型
   */
  public <T> Map<String, T> getMap(String key, Class<T> valueCls) {
    Map<String, T> map = new HashMap();

    String result = mSp.getString(key, null);
    if (result != null) {
      JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
      Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
      for (Map.Entry<String, JsonElement> entry : entrySet) {
        map.put(entry.getKey(), mGson.fromJson(entry.getValue(), valueCls));
      }
    }
    return map;
  }

  /**
   * 是否包含某key
   */
  public boolean containsKey(String key) {
    return mSp.contains(key);
  }
}
