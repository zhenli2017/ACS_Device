
package com.thdtek.acs.terminal.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;

public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    private static String FILE_NAME = "SHARE_DATA";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */


    public static void put(Context context, String key, Object object) {
        put(context, key, object, true);
    }

    public static void put(Context context, String key, Object object, boolean commit) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        if (commit) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(Context context, Map<String, String> map) {
        put(context, map, true);
    }

    public static void put(Context context, Map<String, String> map, boolean commit) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        ArrayList<String> list = new ArrayList<>(map.keySet());
        for (String key : list) {
            editor.putString(key, map.get(key));
        }
        if (commit) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static Object get(Context context, String key) {
        return get(context, key, "");
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(Context context, String key) {
        remove(context, key, false);
    }

    public static void remove(Context context, String key, boolean commit) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        if (commit) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        clear(context, false);
    }

    public static void clear(Context context, boolean commit) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        if (commit) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }
}
