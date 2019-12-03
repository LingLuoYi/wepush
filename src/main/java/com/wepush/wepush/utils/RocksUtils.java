package com.wepush.wepush.utils;

import com.wepush.wepush.WepushApplication;
import com.wepush.wepush.config.db.RocksDBConfig;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import java.util.ArrayList;
import java.util.List;

public class RocksUtils {

    public static void set(String key, String value) {
        try {
            RocksDB db = WepushApplication.ac.getBean(RocksDBConfig.class).Rocksdb();
            db.put(key.getBytes(), value.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        String data = null;
        try {
            RocksDB db = WepushApplication.ac.getBean(RocksDBConfig.class).Rocksdb();
            if (db.get(key.getBytes()) != null)
                data = new String(db.get(key.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void remove(String key) {
        try {
            RocksDB db = WepushApplication.ac.getBean(RocksDBConfig.class).Rocksdb();
            db.delete(key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getLike(String key) {
        List<String> values = new ArrayList<>();
        try {
            RocksDB db = WepushApplication.ac.getBean(RocksDBConfig.class).Rocksdb();
            final RocksIterator iterator = db.newIterator();
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                String old = new String(iterator.value());
                if (old.contains(key))
                    values.add(old);
            }
            return values;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
