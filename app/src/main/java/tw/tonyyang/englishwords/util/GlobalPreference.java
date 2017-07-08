package tw.tonyyang.englishwords.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static tw.tonyyang.englishwords.util.IPreference.DataType.INTEGER;

/**
 * Created by tonyyang on 2017/7/8.
 * Reference: http://www.jianshu.com/p/fcd75a324c35
 */

public class GlobalPreference implements IPreference {

    private SharedPreferences preferences;

    private static final String FILENAME = "global_data";

    private static final Object lock = new Object();

    private static IPreference iPreference;

    public static final String FLAG_PERMISSIONS_STORAGE = "flag_permissions_storage";

    public static IPreference getPreference(Context context) {
        synchronized (lock) {
            if (iPreference == null) {
                initPreference(context, FILENAME);
            }
        }
        return iPreference;
    }

    private static synchronized void initPreference(Context context, String fileName) {
        if (iPreference == null) {
            iPreference = new GlobalPreference(context, fileName);
        }
    }

    public GlobalPreference(Context context) {
        this(context, FILENAME);
    }

    public GlobalPreference(Context context, String fileName) {
        preferences = context.getSharedPreferences(fileName, 0);
    }

    @Override
    public void put(String key, Object value) {
        SharedPreferences.Editor edit = preferences.edit();
        put(edit, key, value);
        edit.apply();
    }

    /**
     * 保存一个Map集合
     * @param map
     */
    @Override
    public <T> void putAll(Map<String, T> map) {
        SharedPreferences.Editor edit = preferences.edit();
        for(Map.Entry<String, T> entry : map.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            put(edit, key, value);
        }
        edit.apply();
    }

    @Override
    public void putAll(String key, List<String> list) {
        putAll(key, list, new ComparatorImpl());
    }

    @Override
    public void putAll(String key, List<String> list, Comparator<String> comparator) {
        Set<String> set = new TreeSet<>(comparator);
        for(String value : list){
            set.add(value);
        }
        preferences.edit().putStringSet(key, set).apply();
    }

    /**
     * 根据key取出一个数据
     * @param key 键
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, DataType type) {
        return (T) getValue(key, type);
    }

    @Override
    public Map<String, ?> getAll() {
        return  preferences.getAll();
    }

    @Override
    public List<String> getAll(String key) {
        List<String> list = new ArrayList<String>();
        Set<String> set = get(key, DataType.STRING_SET);
        for(String value : set){
            list.add(value);
        }
        return list;
    }

    @Override
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    @Override
    public void removeAll(List<String> keys) {
        SharedPreferences.Editor edit = preferences.edit();
        for (String k : keys) {
            edit.remove(k);
        }
        edit.apply();
    }

    @Override
    public void removeAll(String[] keys) {
        removeAll(Arrays.asList(keys));
    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @Override
    public void clear() {
        preferences.edit().clear().apply();
    }

    @Override
    public String getString(String key) {
        return get(key, DataType.STRING);
    }

    @Override
    public float getFloat(String key) {
        return get(key, DataType.FLOAT);
    }

    @Override
    public int getInteger(String key) {
        return get(key, INTEGER);
    }

    @Override
    public long getLong(String key) {
        return get(key, DataType.LONG);
    }

    @Override
    public Set<String> getSet(String key) {
        return get(key, DataType.STRING_SET);
    }

    @Override
    public boolean getBoolean(String key) {
        return get(key, DataType.BOOLEAN);
    }

    /**
     * 保存数据
     * @param editor
     * @param key
     * @param obj
     */
    @SuppressWarnings("unchecked")
    private void put(SharedPreferences.Editor editor, String key, Object obj) {
        // key 不为null时再存入，否则不存储
        if (key != null){
            if (obj instanceof Integer){
                editor.putInt(key, (Integer)obj);
            } else if (obj instanceof Long){
                editor.putLong(key, (Long)obj);
            } else if (obj instanceof Boolean){
                editor.putBoolean(key, (Boolean)obj);
            } else if (obj instanceof Float){
                editor.putFloat(key, (Float) obj);
            } else if (obj instanceof Set){
                editor.putStringSet(key, (Set<String>) obj);
            } else if (obj instanceof String){
                editor.putString(key, String.valueOf(obj));
            }
        }
    }

    /**
     * 根据key和类型取出数据
     * @param key
     * @return
     */
    private Object getValue(String key, DataType type){
        switch (type) {
            case INTEGER:
                return preferences.getInt(key, -1);
            case FLOAT:
                return preferences.getFloat(key, -1f);
            case BOOLEAN:
                return preferences.getBoolean(key, false);
            case LONG:
                return preferences.getLong(key, -1L);
            case STRING:
                return preferences.getString(key, null);
            case STRING_SET:
                return preferences.getStringSet(key, null);
            default: // 默认取出String类型的数据
                return null;
        }
    }
}
